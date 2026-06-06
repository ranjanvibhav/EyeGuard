package com.eyeguard.view;

import com.eyeguard.exception.EyeGuardException;
import com.eyeguard.viewmodel.MainWindowViewModel;
import com.eyeguard.viewmodel.SettingsWindowViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for the MainWindow FXML layout.
 * Coordinates bindings between UI controls and the MainWindowViewModel.
 */
public class MainWindowController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    private Button settingsButton;

    @FXML
    private Label statusBadge;

    @FXML
    private Label countdownLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private ProgressBar timerProgressBar;

    @FXML
    private Label breaksTakenLabel;

    @FXML
    private Label snoozedLabel;

    @FXML
    private Label complianceLabel;

    @FXML
    private Label sessionDurationLabel;

    @FXML
    private Label streakLabel;

    private MainWindowViewModel viewModel;

    /**
     * Initializes the controller class. This is called automatically by FXMLLoader.
     *
     * @param url the location used to resolve relative paths for the root object, or null
     * @param resources the resources used to localize the root object, or null
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resources) {
        LOGGER.debug("MainWindowController initialized by FXMLLoader");
        // Static FXML configuration can go here. Initial label text for duration label is static.
        durationLabel.setText("out of 20:00 minutes");
    }

    /**
     * Sets the ViewModel and binds UI controls to the ViewModel properties.
     *
     * @param viewModel the presentation state view model
     */
    public void setViewModel(final MainWindowViewModel viewModel) {
        this.viewModel = viewModel;
        performBindings();
        LOGGER.debug("MainWindowViewModel bound to MainWindowController");
    }

    /**
     * Performs property bindings between the UI elements and the ViewModel.
     */
    private void performBindings() {
        if (viewModel == null) {
            return;
        }

        // Bind text and progress properties
        countdownLabel.textProperty().bind(viewModel.nextBreakCountdownProperty());
        timerProgressBar.progressProperty().bind(viewModel.timerProgressProperty());
        statusBadge.textProperty().bind(viewModel.statusTextProperty());

        breaksTakenLabel.textProperty().bind(viewModel.breaksTakenProperty().asString());
        snoozedLabel.textProperty().bind(viewModel.snoozedCountProperty().asString());
        complianceLabel.textProperty().bind(viewModel.compliancePercentProperty());
        sessionDurationLabel.textProperty().bind(viewModel.sessionDurationProperty());
        streakLabel.textProperty().bind(viewModel.streakTextProperty());

        // Bind style class for the status badge
        statusBadge.getStyleClass().add(viewModel.getStatusStyle());
        viewModel.statusStyleProperty().addListener((observable, oldStyle, newStyle) -> {
            if (oldStyle != null) {
                statusBadge.getStyleClass().remove(oldStyle);
            }
            if (newStyle != null) {
                statusBadge.getStyleClass().add(newStyle);
            }
        });
    }

    /**
     * Handles the Action event for the Snooze button.
     */
    @FXML
    private void handleSnooze() {
        LOGGER.info("Snooze button clicked");
    }

    /**
     * Handles the Action event for the Pause button.
     */
    @FXML
    private void handlePause() {
        LOGGER.info("Pause button clicked");
    }

    /**
     * Handles the Action event for the Settings button.
     */
    @FXML
    private void handleSettings() {
        LOGGER.info("Settings button clicked");
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings-window.fxml"));
            final Parent root = loader.load();

            final SettingsWindowViewModel settingsViewModel = new SettingsWindowViewModel();
            final SettingsWindowController controller = loader.getController();
            controller.setViewModel(settingsViewModel);

            configureAndShowSettings(root);
            LOGGER.info("Settings window opened");
        } catch (final IOException exception) {
            LOGGER.error("Failed to load settings window FXML", exception);
            throw new EyeGuardException("Failed to open settings window", exception);
        }
    }

    /**
     * Configures the stage properties and displays the Settings stage as modal.
     *
     * @param root the loaded parent node
     */
    private void configureAndShowSettings(final Parent root) {
        final Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner((Stage) settingsButton.getScene().getWindow());

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("EyeGuard — Settings");
        stage.setWidth(520.0);
        stage.setHeight(600.0);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.showAndWait();
    }
}
