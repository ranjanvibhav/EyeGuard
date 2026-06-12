package com.eyeguard.service;

import com.eyeguard.exception.EyeGuardException;
import com.eyeguard.view.BreakOverlayController;
import com.eyeguard.viewmodel.BreakOverlayViewModel;
import com.eyeguard.util.IconLoader;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.User32;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the OverlayService managing the transparent full-screen Stage for breaks.
 */
public class OverlayServiceImpl implements OverlayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayServiceImpl.class);
    private static final String FXML_PATH = "/fxml/break-overlay.fxml";

    private final BreakOverlayViewModel viewModel;
    private Stage overlayStage;

    /**
     * Constructs the OverlayServiceImpl with the specified ViewModel.
     *
     * @param viewModel the view model to bind to the overlay controller
     */
    public OverlayServiceImpl(final BreakOverlayViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Displays the break overlay in full-screen mode.
     * Must be called on the JavaFX Application Thread.
     */
    @Override
    public void showOverlay() {
        if (!Platform.isFxApplicationThread()) {
            LOGGER.error("showOverlay must be called on FX Thread");
            return;
        }
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            final Parent root = loader.load();
            final BreakOverlayController controller = loader.getController();
            controller.setViewModel(viewModel);
            configureOverlayStage(root);
            pauseMediaIfWindows();
        } catch (final IOException exception) {
            throw new EyeGuardException("Failed to load break overlay UI", exception);
        }
    }

    private void configureOverlayStage(final Parent root) {
        overlayStage = new Stage(StageStyle.TRANSPARENT);
        final Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        overlayStage.setScene(scene);
        overlayStage.setAlwaysOnTop(true);
        overlayStage.setFullScreen(true);
        final javafx.scene.image.Image icon = IconLoader.loadJavaFXImage(64, 64);
        if (icon != null) {
            overlayStage.getIcons().add(icon);
        }
        overlayStage.show();
        overlayStage.toFront();
    }

    /**
     * Dismisses the break overlay stage.
     * Can be called from any thread; marshals to FX thread if necessary.
     */
    @Override
    public void hideOverlay() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::hideOverlay);
            return;
        }
        if (overlayStage != null) {
            overlayStage.close();
            overlayStage = null;
            LOGGER.info("Break overlay hidden");
        } else {
            LOGGER.warn("hideOverlay called but overlayStage is already null");
        }
    }

    private void pauseMediaIfWindows() {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            try {
                final HWND broadcast = new HWND(Pointer.createConstant(0xFFFF));
                final int WM_APPCOMMAND = 0x0319;
                final int APPCOMMAND_MEDIA_PAUSE = 47;
                final LPARAM lParam = new LPARAM((long) APPCOMMAND_MEDIA_PAUSE << 16);
                User32.INSTANCE.PostMessage(broadcast, WM_APPCOMMAND, new WPARAM(0), lParam);
                LOGGER.info("System-wide media pause command sent via PostMessage");
            } catch (final Exception e) {
                LOGGER.warn("Failed to send system-wide media pause: {}", e.getMessage());
            }
        }
    }
}
