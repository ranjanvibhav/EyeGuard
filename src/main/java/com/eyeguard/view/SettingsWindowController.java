package com.eyeguard.view;

import com.eyeguard.util.ToggleSwitch;
import com.eyeguard.viewmodel.SettingsWindowViewModel;
import com.eyeguard.service.ConfigurationService;
import com.eyeguard.model.Settings;
import com.eyeguard.model.SettingsConstraints;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for the SettingsWindow FXML layout.
 * Manages FXML bindings, choice box populating, and window closing events.
 */
public class SettingsWindowController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsWindowController.class);

    @FXML
    private Button closeButton;

    @FXML
    private TabPane settingsTabPane;

    @FXML
    private Slider reminderIntervalSlider;

    @FXML
    private Label reminderIntervalLabel;

    @FXML
    private Slider breakDurationSlider;

    @FXML
    private Label breakDurationLabel;

    @FXML
    private ChoiceBox<String> snoozeDurationChoice;

    @FXML
    private ToggleSwitch workingHoursToggle;

    @FXML
    private HBox timeRangeContainer;

    @FXML
    private ChoiceBox<String> workStartChoice;

    @FXML
    private ChoiceBox<String> workEndChoice;

    @FXML
    private ToggleSwitch weekendToggle;

    @FXML
    private ToggleSwitch fullscreenPauseToggle;

    @FXML
    private ToggleSwitch idleDetectionToggle;


    @FXML
    private ToggleSwitch startupToggle;

    @FXML
    private Label settingsErrorLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    private SettingsWindowViewModel viewModel;
    private ConfigurationService configurationService;
    private Runnable applySettingsCallback;

    /**
     * Initializes the controller class, populates options, and binds static structures.
     *
     * @param url the location used to resolve relative paths for the root object, or null
     * @param resources the resources used to localize the root object, or null
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resources) {
        LOGGER.debug("SettingsWindowController initialized by FXMLLoader");
        populateChoiceBoxes();

        // Bind slider text labels to slider values
        reminderIntervalLabel.textProperty().bind(reminderIntervalSlider.valueProperty().asString("%.0f min"));
        breakDurationLabel.textProperty().bind(breakDurationSlider.valueProperty().asString("%.0f sec"));

        // Bind visibility and managed state of the time range container to the working hours toggle
        timeRangeContainer.visibleProperty().bind(workingHoursToggle.selectedProperty());
        timeRangeContainer.managedProperty().bind(workingHoursToggle.selectedProperty());
    }

    /**
     * Sets the view model and performs bidirectional property bindings.
     *
     * @param viewModel the view model to bind
     */
    public void setViewModel(final SettingsWindowViewModel viewModel) {
        this.viewModel = viewModel;
        performBindings();
        LOGGER.debug("SettingsWindowViewModel bound to SettingsWindowController");
    }

    /**
     * Populates values for start time, end time, and default snooze duration choice boxes.
     */
    private void populateChoiceBoxes() {
        snoozeDurationChoice.getItems().addAll("5 minutes", "10 minutes", "15 minutes", "30 minutes");

        workStartChoice.getItems().addAll(
                "6:00 AM", "6:30 AM", "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM",
                "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM"
        );

        workEndChoice.getItems().addAll(
                "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
                "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM",
                "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM",
                "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM", "11:00 PM"
        );
    }

    /**
     * Binds UI controls bidirectionally to the ViewModel properties.
     */
    private void performBindings() {
        if (viewModel == null) return;
        snoozeDurationChoice.valueProperty().bindBidirectional(viewModel.snoozeDurationProperty());
        workStartChoice.valueProperty().bindBidirectional(viewModel.workStartTimeProperty());
        workEndChoice.valueProperty().bindBidirectional(viewModel.workEndTimeProperty());
        workingHoursToggle.selectedProperty().bindBidirectional(viewModel.workingHoursEnabledProperty());
        weekendToggle.selectedProperty().bindBidirectional(viewModel.weekendRemindersEnabledProperty());
        fullscreenPauseToggle.selectedProperty().bindBidirectional(viewModel.fullscreenPauseEnabledProperty());
        idleDetectionToggle.selectedProperty().bindBidirectional(viewModel.idleDetectionEnabledProperty());
        startupToggle.selectedProperty().bindBidirectional(viewModel.launchAtStartupEnabledProperty());
        settingsErrorLabel.textProperty().bind(viewModel.errorMessageProperty());
        bindSliders();
    }

    /**
     * Map double slider value properties to integer view model properties using change listeners.
     */
    private void bindSliders() {
        reminderIntervalSlider.setValue(viewModel.getReminderIntervalMinutes());
        reminderIntervalSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            final int intVal = newVal.intValue();
            if (viewModel.getReminderIntervalMinutes() != intVal) {
                viewModel.reminderIntervalMinutesProperty().set(intVal);
            }
        });
        viewModel.reminderIntervalMinutesProperty().addListener((obs, oldVal, newVal) -> {
            final double dblVal = newVal.doubleValue();
            if (reminderIntervalSlider.getValue() != dblVal) {
                reminderIntervalSlider.setValue(dblVal);
            }
        });

        breakDurationSlider.setValue(viewModel.getBreakDurationSeconds());
        breakDurationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            final int intVal = newVal.intValue();
            if (viewModel.getBreakDurationSeconds() != intVal) {
                viewModel.breakDurationSecondsProperty().set(intVal);
            }
        });
        viewModel.breakDurationSecondsProperty().addListener((obs, oldVal, newVal) -> {
            final double dblVal = newVal.doubleValue();
            if (breakDurationSlider.getValue() != dblVal) {
                breakDurationSlider.setValue(dblVal);
            }
        });
    }

    /**
     * Closes the settings modal window.
     */
    private void closeWindow() {
        final Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the Action event for the Save button.
     */
    @FXML
    private void handleSave() {
        LOGGER.info("Save clicked");
        final com.eyeguard.model.Settings settings = new com.eyeguard.model.Settings();
        settings.setReminderIntervalMinutes(viewModel.getReminderIntervalMinutes());
        settings.setBreakDurationSeconds(viewModel.getBreakDurationSeconds());
        settings.setSnoozeDurationMinutes(parseSnoozeDuration(viewModel.getSnoozeDuration()));
        settings.setWorkingHoursEnabled(viewModel.isWorkingHoursEnabled());
        settings.setWorkStartTime(convertTo24Hour(viewModel.getWorkStartTime()));
        settings.setWorkEndTime(convertTo24Hour(viewModel.getWorkEndTime()));
        settings.setWeekendRemindersEnabled(viewModel.isWeekendRemindersEnabled());
        settings.setFullscreenPauseEnabled(viewModel.isFullscreenPauseEnabled());
        settings.setIdleDetectionEnabled(viewModel.isIdleDetectionEnabled());
        settings.setSoundAlertsEnabled(viewModel.isSoundAlertsEnabled());
        settings.setLaunchAtStartupEnabled(viewModel.isLaunchAtStartupEnabled());
        saveAndClose(settings);
    }

    private void saveAndClose(final com.eyeguard.model.Settings settings) {
        try {
            configurationService.saveSettings(settings);
            if (applySettingsCallback != null) {
                applySettingsCallback.run();
            }
            LOGGER.info("Settings saved successfully");
            closeWindow();
        } catch (final Exception e) {
            viewModel.errorMessageProperty().set(e.getMessage());
        }
    }

    private int parseSnoozeDuration(final String text) {
        try {
            final String numStr = text.replaceAll("[^0-9]", "");
            return Integer.parseInt(numStr);
        } catch (final Exception e) {
            return 5;
        }
    }

    private String convertTo24Hour(final String time12h) {
        try {
            final java.time.format.DateTimeFormatter format12 =
                    java.time.format.DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.US);
            final java.time.LocalTime time = java.time.LocalTime.parse(time12h, format12);
            return time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        } catch (final Exception e) {
            return com.eyeguard.model.SettingsConstraints.DEFAULT_WORK_START_TIME;
        }
    }

    private String convertTo12Hour(final String time24h) {
        try {
            final java.time.LocalTime time = java.time.LocalTime.parse(time24h,
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            return time.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.US));
        } catch (final Exception e) {
            return "9:00 AM";
        }
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
        populateViewModelFromSettings();
    }

    public void setApplySettingsCallback(final Runnable callback) {
        this.applySettingsCallback = callback;
    }

    private void populateViewModelFromSettings() {
        if (configurationService == null || viewModel == null) {
            return;
        }
        try {
            final com.eyeguard.model.Settings s = configurationService.loadSettings();
            viewModel.reminderIntervalMinutesProperty().set(s.getReminderIntervalMinutes());
            viewModel.breakDurationSecondsProperty().set(s.getBreakDurationSeconds());
            viewModel.snoozeDurationProperty().set(s.getSnoozeDurationMinutes() + " minutes");
            populateWorkingHoursInViewModel(s);
            viewModel.fullscreenPauseEnabledProperty().set(s.isFullscreenPauseEnabled());
            viewModel.idleDetectionEnabledProperty().set(s.isIdleDetectionEnabled());
            viewModel.soundAlertsEnabledProperty().set(s.isSoundAlertsEnabled());
            viewModel.launchAtStartupEnabledProperty().set(s.isLaunchAtStartupEnabled());
        } catch (final Exception e) {
            LOGGER.warn("Failed to load settings: {}", e.getMessage());
        }
    }

    private void populateWorkingHoursInViewModel(final com.eyeguard.model.Settings s) {
        viewModel.workingHoursEnabledProperty().set(s.isWorkingHoursEnabled());
        viewModel.workStartTimeProperty().set(convertTo12Hour(s.getWorkStartTime()));
        viewModel.workEndTimeProperty().set(convertTo12Hour(s.getWorkEndTime()));
        viewModel.weekendRemindersEnabledProperty().set(s.isWeekendRemindersEnabled());
    }

    /**
     * Handles the Action event for the Cancel button.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Cancel clicked");
        closeWindow();
    }

    /**
     * Handles the Action event for the Close (✕) button.
     */
    @FXML
    private void handleClose() {
        LOGGER.info("Close clicked");
        closeWindow();
    }
}
