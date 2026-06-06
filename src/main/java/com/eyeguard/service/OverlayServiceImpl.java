package com.eyeguard.service;

import com.eyeguard.exception.EyeGuardException;
import com.eyeguard.view.BreakOverlayController;
import com.eyeguard.viewmodel.BreakOverlayViewModel;
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
            LOGGER.error("showOverlay must be called on the JavaFX Application Thread");
            return;
        }

        try {
            LOGGER.debug("Loading break overlay UI...");
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            final Parent root = loader.load();

            final BreakOverlayController controller = loader.getController();
            controller.setViewModel(viewModel);

            overlayStage = new Stage(StageStyle.TRANSPARENT);
            final Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            overlayStage.setScene(scene);
            overlayStage.setAlwaysOnTop(true);
            overlayStage.setFullScreen(true);
            overlayStage.show();
            overlayStage.toFront();

            LOGGER.info("Break overlay shown");
        } catch (final IOException exception) {
            LOGGER.error("Failed to load break overlay FXML layout from " + FXML_PATH, exception);
            throw new EyeGuardException("Failed to load break overlay user interface", exception);
        }
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
}
