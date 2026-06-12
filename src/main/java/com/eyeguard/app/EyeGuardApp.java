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
import com.eyeguard.view.SettingsWindowController;
import com.eyeguard.viewmodel.SettingsWindowViewModel;
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
import com.eyeguard.service.WorkingHoursService;
import com.eyeguard.service.WorkingHoursServiceImpl;
import com.eyeguard.service.ToastService;
import com.eyeguard.service.ToastServiceImpl;
import com.eyeguard.service.PreWarningService;
import com.eyeguard.service.PreWarningServiceImpl;
import com.eyeguard.service.StartupService;
import com.eyeguard.service.StartupServiceFactory;
import com.eyeguard.util.IconLoader;
import com.eyeguard.util.SingleInstanceDetector;
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
    private WorkingHoursService workingHoursService;
    private ToastService toastService;
    private PreWarningService preWarningService;
    private StartupService startupService;
    private TrayViewModel trayViewModel;
    private com.eyeguard.service.StatisticsService statisticsService;

    /**
     * Starts the JavaFX application by initializing the primary stage and loading the UI.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(final Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (!SingleInstanceDetector.checkAndRegister(this::showExistingInstance)) {
            handleDuplicateInstance();
            return;
        }
        try {
            LOGGER.info("EyeGuard application starting...");
            loadApplicationSettings();
            initCoreServices();
            setupFullscreenDetection();
            setupWorkingHours();
            initializeTimer();
            loadAndShowUI(primaryStage);
            LOGGER.info("Main window loaded successfully");
        } catch (final Exception exception) {
            LOGGER.error("Failed to initialize EyeGuard", exception);
            throw new EyeGuardException("Failed to initialize EyeGuard user interface", exception);
        }
    }

    private void handleDuplicateInstance() {
        LOGGER.info("Another instance of EyeGuard is already running. Exiting.");
        Platform.exit();
        System.exit(0);
    }

    private void showExistingInstance() {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.show();
                primaryStage.toFront();
            }
        });
    }

    private void initCoreServices() {
        statisticsService = com.eyeguard.service.StatisticsServiceImpl.createDefault();
        timerService = new TimerServiceImpl();
        dndService = new DndServiceImpl(timerService, configurationService);
        breakOverlayViewModel = new BreakOverlayViewModel();
        final com.eyeguard.service.OverlayService overlayService =
                new com.eyeguard.service.OverlayServiceImpl(breakOverlayViewModel);
        breakService = new BreakServiceImpl(timerService, overlayService,
                breakOverlayViewModel, configurationService, statisticsService);
        final SystemIdleProvider idleProvider = IdleProviderFactory.create();
        idleDetectionService = new IdleDetectionServiceImpl(idleProvider);
        initIdleDetection();
    }

    private void initIdleDetection() {
        idleDetectionService.setOnIdleDetected(this::handleIdleDetected);
        idleDetectionService.setOnActivityResumed(this::handleActivityResumed);
        if (currentSettings.isIdleDetectionEnabled()) {
            idleDetectionService.start();
            LOGGER.info("Idle detection enabled");
        } else {
            LOGGER.info("Idle detection disabled in settings");
        }
    }

    private void handleIdleDetected() {
        LOGGER.info("Idle detected — resetting and pausing reminders");
        if (dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
            timerService.reset(getConfiguredIntervalSeconds());
            timerService.pause();
        }
    }

    private void handleActivityResumed() {
        LOGGER.info("Activity resumed — restarting reminders");
        if (timerService.getTimerState() == com.eyeguard.model.TimerState.PAUSED
                && dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
            timerService.resume();
        }
    }

    private int getConfiguredIntervalSeconds() {
        try {
            return configurationService.loadSettings()
                    .getReminderIntervalMinutes() * 60;
        } catch (final SettingsLoadException e) {
            LOGGER.error("Failed to load settings for timer reset", e);
            return com.eyeguard.model.SettingsConstraints.DEFAULT_REMINDER_INTERVAL * 60;
        }
    }

    private void loadAndShowUI(final Stage stage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        final Parent root = loader.load();
        mainViewModel = new MainWindowViewModel(timerService, dndService, fullscreenDetectionService, statisticsService);
        final MainWindowController controller = loader.getController();
        controller.setViewModel(mainViewModel);
        controller.setConfigurationService(configurationService);
        controller.setApplySettingsCallback(() -> Platform.runLater(() -> {
            loadApplicationSettings();
            applySettings(currentSettings);
        }));
        setupStage(stage, new Scene(root));
        setupTray(stage);
        final var icon = IconLoader.loadJavaFXImage(64, 64);
        if (icon != null) stage.getIcons().add(icon);
        if (!getParameters().getRaw().contains("--startup")) stage.show();
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
        dashboardViewModel = new DashboardViewModel(timerService, dndService, idleDetectionService, statisticsService);
        dashboardViewModel.setOnSnoozeQuick(() -> dndService.snooze(5));
        dashboardViewModel.setOnPauseQuick(() -> mainViewModel.handlePause());
        dashboardViewModel.setOnSettingsQuick(this::openSettingsWindow);
        dashboardService = new DashboardServiceImpl(dashboardViewModel);
        initTrayService(stage);
        trayViewModel = new TrayViewModel(timerService, dndService, workingHoursService);
        trayViewModel.pauseMenuItemTextProperty().addListener((obs, old, newVal) ->
            trayService.updatePauseMenuItem(newVal));
        trayViewModel.tooltipTextProperty().addListener((obs, old, newVal) ->
            trayService.updateTooltip(newVal));
        trayViewModel.statusMenuItemTextProperty().addListener((obs, old, newVal) ->
            trayService.updateStatusMenuItem(newVal));
        trayService.initializeTray();
        initWarningAndStartup();
        configureStageClose(stage, trayViewModel);
    }

    private void initTrayService(final Stage stage) {
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
    }

    private void initWarningAndStartup() {
        toastService = new ToastServiceImpl();
        preWarningService = new PreWarningServiceImpl(toastService, trayService);
        preWarningService.attach(timerService);
        startupService = StartupServiceFactory.create();
        final boolean registered = startupService.isRegistered();
        if (registered != currentSettings.isLaunchAtStartupEnabled()) {
            currentSettings.setLaunchAtStartupEnabled(registered);
            try {
                configurationService.saveSettings(currentSettings);
            } catch (final Exception e) {
                LOGGER.error("Failed to save synced startup settings", e);
            }
        }
    }

    private void configureStageClose(final Stage stage, final TrayViewModel trayViewModel) {
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(e -> {
            e.consume();
            stage.hide();
            trayService.updateTooltip(trayViewModel.getTooltipText());
            LOGGER.info("Main window hidden, app running in tray");
        });
    }

    private void applyStartupSetting(final boolean enabled) {
        if (enabled) {
            startupService.register();
        } else {
            startupService.unregister();
        }
    }

    /**
     * Placeholder method to trigger the settings window from the tray menu.
     */
    private void openSettingsWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings-window.fxml"));
            final Parent root = loader.load();
            final SettingsWindowController controller = loader.getController();
            controller.setViewModel(new SettingsWindowViewModel());
            controller.setConfigurationService(configurationService);
            controller.setApplySettingsCallback(() -> Platform.runLater(() -> {
                loadApplicationSettings();
                applySettings(currentSettings);
            }));
            showSettingsStage(root);
        } catch (final IOException e) {
            LOGGER.error("Failed to open settings window", e);
        }
    }

    private void showSettingsStage(final Parent root) {
        final Stage stage = new Stage();
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("EyeGuard — Settings");
        stage.setResizable(false);
        final javafx.scene.image.Image icon = IconLoader.loadJavaFXImage(64, 64);
        if (icon != null) {
            stage.getIcons().add(icon);
        }
        stage.showAndWait();
    }

    private void setupFullscreenDetection() {
        final SystemFullscreenProvider provider = FullscreenProviderFactory.create();
        fullscreenDetectionService = new FullscreenDetectionServiceImpl(provider);
        fullscreenDetectionService.setOnFullscreenEntered(this::handleFullscreenEntered);
        fullscreenDetectionService.setOnFullscreenExited(this::handleFullscreenExited);
        if (currentSettings.isFullscreenPauseEnabled()) {
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

    private void setupWorkingHours() {
        workingHoursService = new WorkingHoursServiceImpl(configurationService);
        if (currentSettings.isWorkingHoursEnabled()) {
            workingHoursService.setOnWorkingHoursStarted(this::handleWorkingHoursStarted);
            workingHoursService.setOnWorkingHoursEnded(this::handleWorkingHoursEnded);
            workingHoursService.start();
            if (!workingHoursService.isWithinWorkingHours()) {
                timerService.pause();
                LOGGER.info("App started outside working hours — timer paused");
            }
        }
    }

    private void handleWorkingHoursStarted() {
        LOGGER.info("Working hours started");
        if (timerService.getTimerState() == com.eyeguard.model.TimerState.PAUSED
                && dndService.getDndState() == com.eyeguard.model.DndState.INACTIVE) {
            timerService.resume();
        }
    }

    private void handleWorkingHoursEnded() {
        LOGGER.info("Working hours ended");
        if (timerService.getTimerState() == com.eyeguard.model.TimerState.RUNNING) {
            timerService.pause();
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
        workingHoursService.reloadSettings();
        applyStartupSetting(settings.isLaunchAtStartupEnabled());
        timerService.reset(settings.getReminderIntervalMinutes() * 60);
        LOGGER.info("Settings applied: {}", settings);
    }

    /**
     * Handles cleanup and logging on application shutdown.
     */
    @Override
    public void stop() {
        stopServices();
        SingleInstanceDetector.shutdown();
        cleanupAppComponents();
        if (statisticsService != null) {
            statisticsService.shutdown();
        }
        LOGGER.info("EyeGuard application stopped");
    }

    private void cleanupAppComponents() {
        if (preWarningService != null) {
            preWarningService.detach();
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
    }

    private void stopServices() {
        if (workingHoursService != null) {
            workingHoursService.stop();
        }
        if (fullscreenDetectionService != null) {
            fullscreenDetectionService.stop();
        }
        if (idleDetectionService != null) {
            idleDetectionService.stop();
        }
        if (timerService != null) {
            timerService.stop();
        }
    }
}
