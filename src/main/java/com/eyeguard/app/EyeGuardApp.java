package com.eyeguard.app;

import com.eyeguard.exception.EyeGuardException;
import com.eyeguard.exception.SettingsLoadException;
import com.eyeguard.model.Settings;
import com.eyeguard.service.ConfigurationService;
import com.eyeguard.service.ConfigurationServiceImpl;
import com.eyeguard.service.DashboardService;
import com.eyeguard.service.DashboardServiceImpl;
import com.eyeguard.service.TimerService;
import com.eyeguard.service.TimerServiceImpl;
import com.eyeguard.service.TrayService;
import com.eyeguard.service.TrayServiceImpl;
import com.eyeguard.view.MainWindowController;
import com.eyeguard.service.DndServiceImpl;
import com.eyeguard.service.IdleDetectionService;
import com.eyeguard.service.IdleDetectionServiceImpl;
import com.eyeguard.service.SystemIdleProvider;
import com.eyeguard.service.IdleProviderFactory;
import com.eyeguard.viewmodel.DashboardViewModel;
import com.eyeguard.viewmodel.MainWindowViewModel;
import com.eyeguard.service.BreakServiceImpl;
import com.eyeguard.service.OverlayServiceImpl;
import com.eyeguard.viewmodel.BreakOverlayViewModel;
import com.eyeguard.viewmodel.TrayViewModel;
import com.eyeguard.service.FullscreenDetectionService;
import com.eyeguard.service.FullscreenDetectionServiceImpl;
import com.eyeguard.service.SystemFullscreenProvider;
import com.eyeguard.service.FullscreenProviderFactory;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for EyeGuard.
 * Manages the JavaFX application lifecycle, including system tray icon wiring.
 */
public class EyeGuardApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(EyeGuardApp.class);

    private static final String APPLICATION_TITLE = "EyeGuard";
    private static final double STAGE_WIDTH = 420.0;
    private static final double STAGE_HEIGHT = 560.0;
    private static final String FXML_PATH = "/fxml/main-window.fxml";

    private Stage primaryStage;
    private TrayService trayService;
    private DashboardViewModel dashboardViewModel;
    private DashboardService dashboardService;
    private ConfigurationService configurationService;
    private Settings currentSettings;
    private TimerService timerService;
    private BreakServiceImpl breakService;
    private BreakOverlayViewModel breakOverlayViewModel;
    private DndServiceImpl dndService;
    private MainWindowViewModel mainViewModel;
    private IdleDetectionService idleDetectionService;
    private FullscreenDetectionService fullscreenDetectionService;

    /**
     * Starts the JavaFX application by initializing the primary stage and loading the UI.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(final Stage primaryStage) {
        try {
            LOGGER.info("EyeGuard application starting...");
            this.primaryStage = primaryStage;

            loadApplicationSettings();
            timerService = new TimerServiceImpl();
            dndService = new DndServiceImpl(timerService, configurationService);
            breakOverlayViewModel = new BreakOverlayViewModel();
            final com.eyeguard.service.OverlayService overlayService = new com.eyeguard.service.OverlayServiceImpl(breakOverlayViewModel);
            breakService = new BreakServiceImpl(timerService, overlayService, breakOverlayViewModel, configurationService);

            final SystemIdleProvider idleProvider = IdleProviderFactory.create();
            idleDetectionService = new IdleDetectionServiceImpl(idleProvider);
            if (currentSettings.isIdleDetectionEnabled()) {
                idleDetectionService.setOnIdleDetected(() -> {
                    LOGGER.info("Idle detected — pausing reminders");
                    if (dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
                        timerService.pause();
                    }
                });
                idleDetectionService.setOnActivityResumed(() -> {
                    LOGGER.info("Activity resumed — restarting reminders");
                    if (timerService.getTimerState() == com.eyeguard.model.TimerState.PAUSED
                            && dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
                        timerService.resume();
                    }
                });
                idleDetectionService.start();
                LOGGER.info("Idle detection enabled");
            } else {
                LOGGER.info("Idle detection disabled in settings");
            }

            setupFullscreenDetection();

            initializeTimer();

            final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            final Parent root = loader.load();

            mainViewModel = new MainWindowViewModel(timerService, dndService, fullscreenDetectionService);
            final MainWindowController controller = loader.getController();
            controller.setViewModel(mainViewModel);

            final Scene scene = new Scene(root);
            setupStage(primaryStage, scene);
            setupTray(primaryStage);
            primaryStage.show();

            LOGGER.info("Main window loaded successfully");
        } catch (final IOException exception) {
            LOGGER.error("Failed to load FXML layout from " + FXML_PATH, exception);
            throw new EyeGuardException("Failed to initialize EyeGuard user interface", exception);
        }
    }

    private void loadApplicationSettings() {
        configurationService = ConfigurationServiceImpl.createDefault();
        try {
            currentSettings = configurationService.loadSettings();
            LOGGER.info("Settings loaded: {}", currentSettings);
        } catch (final SettingsLoadException e) {
            LOGGER.error("Failed to load settings, using defaults", e);
            currentSettings = configurationService.getDefaultSettings();
        }
    }

    private void initializeTimer() {
        final int totalSeconds = currentSettings.getReminderIntervalMinutes() * 60;
        timerService.setOnBreakDue(breakService::startBreak);
        timerService.start(totalSeconds);
        LOGGER.info("Timer started with interval: {}min", currentSettings.getReminderIntervalMinutes());
    }

    /**
     * Configures the visual properties of the stage.
     *
     * @param stage the stage to configure
     * @param scene the scene to bind to the stage
     */
    private void setupStage(final Stage stage, final Scene scene) {
        stage.setScene(scene);
        stage.setTitle(APPLICATION_TITLE);
        stage.setWidth(STAGE_WIDTH);
        stage.setHeight(STAGE_HEIGHT);
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    /**
     * Instantiates the TrayViewModel and initializes the SystemTray service.
     *
     * @param stage the primary stage of the application
     */
    private void setupTray(final Stage stage) {
        dashboardViewModel = new DashboardViewModel(idleDetectionService);
        dashboardService = new DashboardServiceImpl(dashboardViewModel);

        final TrayViewModel trayViewModel = new TrayViewModel(timerService, dndService);
        trayService = new TrayServiceImpl(
            stage::show,
            dashboardService::showDashboard,
            () -> dndService.snooze(5),
            () -> dndService.snooze(10),
            () -> dndService.enableMeetingMode(30),
            () -> dndService.enableMeetingMode(60),
            () -> mainViewModel.handlePause(),
            this::openSettingsWindow,
            Platform::exit
        );
        trayViewModel.pauseMenuItemTextProperty().addListener((obs, old, newVal) ->
            trayService.updatePauseMenuItem(newVal));
        trayService.initializeTray();

        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(e -> {
            e.consume();
            stage.hide();
            trayService.updateTooltip(trayViewModel.getTooltipText());
            LOGGER.info("Main window hidden, app running in tray");
        });
    }

    /**
     * Placeholder method to trigger the settings window from the tray menu.
     */
    private void openSettingsWindow() {
        LOGGER.info("Opening settings from tray");
    }

    private void setupFullscreenDetection() {
        final SystemFullscreenProvider provider = FullscreenProviderFactory.create();
        fullscreenDetectionService = new FullscreenDetectionServiceImpl(provider);
        if (currentSettings.isFullscreenPauseEnabled()) {
            fullscreenDetectionService.setOnFullscreenEntered(this::handleFullscreenEntered);
            fullscreenDetectionService.setOnFullscreenExited(this::handleFullscreenExited);
            fullscreenDetectionService.start();
            LOGGER.info("Fullscreen detection enabled");
        } else {
            LOGGER.info("Fullscreen detection disabled in settings");
        }
    }

    private void handleFullscreenEntered() {
        LOGGER.info("Fullscreen entered — pausing timer");
        if (timerService.getTimerState() == com.eyeguard.model.TimerState.RUNNING
                && dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
            timerService.pause();
        }
    }

    private void handleFullscreenExited() {
        LOGGER.info("Fullscreen exited — resuming timer");
        if (timerService.getTimerState() == com.eyeguard.model.TimerState.PAUSED
                && dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
            timerService.resume();
        }
    }

    private void applySettings(final Settings settings) {
        if (settings.isIdleDetectionEnabled()) {
            idleDetectionService.start();
        } else {
            idleDetectionService.stop();
        }
        if (settings.isFullscreenPauseEnabled()) {
            fullscreenDetectionService.start();
        } else {
            fullscreenDetectionService.stop();
        }
        timerService.reset(settings.getReminderIntervalMinutes() * 60);
        LOGGER.info("Settings applied: {}", settings);
    }

    /**
     * Handles cleanup and logging on application shutdown.
     */
    @Override
    public void stop() {
        if (fullscreenDetectionService != null) {
            fullscreenDetectionService.stop();
        }
        if (idleDetectionService != null) {
            idleDetectionService.stop();
        }
        if (timerService != null) {
            timerService.stop();
        }
        if (breakService != null) {
            breakService.shutdown();
        }
        if (dndService != null) {
            dndService.shutdown();
        }
        if (trayService != null) {
            trayService.dispose();
        }
        LOGGER.info("EyeGuard application stopped");
    }
}
