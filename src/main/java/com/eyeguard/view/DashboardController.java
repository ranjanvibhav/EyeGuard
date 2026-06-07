package com.eyeguard.view;

import com.eyeguard.viewmodel.DashboardViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for the dashboard popup UI.
 * Binds JavaFX elements to DashboardViewModel properties and handles close/quick-action events.
 */
public class DashboardController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    @FXML private StackPane rootPane;
    @FXML private Label activeStatusLabel;
    @FXML private Button closeDashboardButton;
    @FXML private Label nextBreakLabel;
    @FXML private ProgressBar timerProgressBar;
    @FXML private Label breaksTakenValue;
    @FXML private Label snoozedValue;
    @FXML private Label complianceValue;
    @FXML private Label streakValue;
    @FXML private Label sessionDurationLabel;
    @FXML private Label idleStatusLabel;
    @FXML private Button snoozeQuickButton;
    @FXML private Button pauseQuickButton;
    @FXML private Button settingsQuickButton;

    private DashboardViewModel viewModel;
    private Stage stage;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        LOGGER.debug("DashboardController initialized by FXMLLoader");
    }

    /**
     * Sets the ViewModel and binds UI properties.
     *
     * @param viewModel the view model to bind
     */
    public void setViewModel(final DashboardViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties();
    }

    private void bindProperties() {
        activeStatusLabel.textProperty().bind(viewModel.activeStatusTextProperty());
        nextBreakLabel.textProperty().bind(viewModel.nextBreakCountdownProperty());
        timerProgressBar.progressProperty().bind(viewModel.timerProgressProperty());
        breaksTakenValue.textProperty().bind(viewModel.breaksTakenProperty().asString());
        snoozedValue.textProperty().bind(viewModel.snoozedCountProperty().asString());
        complianceValue.textProperty().bind(viewModel.compliancePercentProperty());
        streakValue.textProperty().bind(viewModel.streakDaysProperty().asString());
        sessionDurationLabel.textProperty().bind(viewModel.sessionDurationProperty());
        idleStatusLabel.textProperty().bind(viewModel.idleStatusTextProperty());

        viewModel.idleStatusStyleProperty().addListener((obs, oldVal, newVal) -> {
            idleStatusLabel.getStyleClass().removeAll("status-text-active", "status-text-inactive", "status-text-idle");
            if (newVal != null && !newVal.isEmpty()) {
                idleStatusLabel.getStyleClass().add(newVal);
            }
        });
    }

    /**
     * Sets the Stage and hooks up focus lost listeners.
     *
     * @param stage the stage wrapper
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                stage.hide();
                LOGGER.info("Dashboard dismissed — focus lost");
            }
        });
    }

    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.hide();
        }
        LOGGER.info("Dashboard closed via close button");
    }

    @FXML
    private void handleSnoozeQuick() {
        LOGGER.info("Quick snooze from dashboard");
    }

    @FXML
    private void handlePauseQuick() {
        LOGGER.info("Quick pause from dashboard");
    }

    @FXML
    private void handleSettingsQuick() {
        LOGGER.info("Quick settings from dashboard");
    }
}
