package com.eyeguard.view;

import com.eyeguard.util.CountdownRingDrawer;
import com.eyeguard.viewmodel.BreakOverlayViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for the BreakOverlay FXML layout.
 * Manages bindings to the BreakOverlayViewModel, renders the canvas ring,
 * and plays the breathing circle animation.
 */
public class BreakOverlayController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BreakOverlayController.class);

    private static final double BREATH_DURATION_SECONDS = 2.0;
    private static final double BREATH_SCALE_TO = 1.15;
    private static final double BREATH_OPACITY_FROM = 0.3;
    private static final double BREATH_OPACITY_TO = 0.6;

    private static final String COLOR_TRACK = "#2A3F54";
    private static final String COLOR_PROGRESS = "#2EC4B6";
    private static final double DEFAULT_PROGRESS = 1.0;

    @FXML
    private StackPane rootPane;

    @FXML
    private Canvas countdownCanvas;

    @FXML
    private Circle breathingCircle;

    @FXML
    private Label countdownLabel;

    @FXML
    private Button snoozeButton;

    @FXML
    private Button doneButton;

    private BreakOverlayViewModel viewModel;

    /**
     * Initializes the controller. Sets up static elements and triggers the breathing animation.
     *
     * @param url the location used to resolve relative paths for the root object, or null
     * @param resources the resources used to localize the root object, or null
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resources) {
        LOGGER.debug("BreakOverlayController initialized by FXMLLoader");
        startBreathingAnimation();
    }

    /**
     * Sets the ViewModel and performs bindings.
     *
     * @param viewModel the view model containing state properties
     */
    public void setViewModel(final BreakOverlayViewModel viewModel) {
        this.viewModel = viewModel;
        performBindings();
        drawPlaceholderRing();
        LOGGER.debug("BreakOverlayViewModel bound to BreakOverlayController");
    }

    /**
     * Performs property bindings between the UI controls and the ViewModel.
     */
    private void performBindings() {
        if (viewModel == null) {
            return;
        }

        countdownLabel.textProperty().bind(viewModel.countdownTextProperty());
        doneButton.disableProperty().bind(viewModel.doneButtonEnabledProperty().not());
    }

    /**
     * Draws the initial placeholder countdown ring.
     */
    private void drawPlaceholderRing() {
        final double progress = viewModel != null ? viewModel.getRingProgress() : DEFAULT_PROGRESS;
        CountdownRingDrawer.drawRing(
                countdownCanvas,
                progress,
                Color.web(COLOR_TRACK),
                Color.web(COLOR_PROGRESS)
        );
    }

    /**
     * Configures and starts the programmatic breathing animation on the breathing circle.
     */
    private void startBreathingAnimation() {
        final ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(BREATH_DURATION_SECONDS), breathingCircle);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(BREATH_SCALE_TO);
        scaleTransition.setToY(BREATH_SCALE_TO);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.setAutoReverse(true);

        final FadeTransition fadeTransition = new FadeTransition(Duration.seconds(BREATH_DURATION_SECONDS), breathingCircle);
        fadeTransition.setFromValue(BREATH_OPACITY_FROM);
        fadeTransition.setToValue(BREATH_OPACITY_TO);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);

        final ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
        parallelTransition.play();
    }

    /**
     * Handles the Action event for the Snooze button.
     */
    @FXML
    private void handleSnooze() {
        LOGGER.info("Snooze clicked from overlay");
    }

    /**
     * Handles the Action event for the Done button.
     */
    @FXML
    private void handleDone() {
        LOGGER.info("Done clicked from overlay");
    }
}
