package com.eyeguard.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the SettingsWindow.
 * Holds and exposes presentation properties representing the EyeGuard configurations.
 */
public class SettingsWindowViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsWindowViewModel.class);

    private static final int INITIAL_REMINDER_INTERVAL = 20;
    private static final int INITIAL_BREAK_DURATION = 20;
    private static final String INITIAL_SNOOZE_DURATION = "5 minutes";
    private static final boolean INITIAL_WORKING_HOURS_ENABLED = true;
    private static final String INITIAL_WORK_START_TIME = "9:00 AM";
    private static final String INITIAL_WORK_END_TIME = "7:00 PM";
    private static final boolean INITIAL_WEEKEND_REMINDERS_ENABLED = false;
    private static final boolean INITIAL_FULLSCREEN_PAUSE_ENABLED = true;
    private static final boolean INITIAL_IDLE_DETECTION_ENABLED = true;
    private static final boolean INITIAL_SOUND_ALERTS_ENABLED = true;
    private static final boolean INITIAL_LAUNCH_AT_STARTUP_ENABLED = false;
    private static final String INITIAL_ERROR_MESSAGE = "";

    private final IntegerProperty reminderIntervalMinutes = new SimpleIntegerProperty(INITIAL_REMINDER_INTERVAL);
    private final IntegerProperty breakDurationSeconds = new SimpleIntegerProperty(INITIAL_BREAK_DURATION);
    private final StringProperty snoozeDuration = new SimpleStringProperty(INITIAL_SNOOZE_DURATION);
    private final BooleanProperty workingHoursEnabled = new SimpleBooleanProperty(INITIAL_WORKING_HOURS_ENABLED);
    private final StringProperty workStartTime = new SimpleStringProperty(INITIAL_WORK_START_TIME);
    private final StringProperty workEndTime = new SimpleStringProperty(INITIAL_WORK_END_TIME);
    private final BooleanProperty weekendRemindersEnabled = new SimpleBooleanProperty(INITIAL_WEEKEND_REMINDERS_ENABLED);
    private final BooleanProperty fullscreenPauseEnabled = new SimpleBooleanProperty(INITIAL_FULLSCREEN_PAUSE_ENABLED);
    private final BooleanProperty idleDetectionEnabled = new SimpleBooleanProperty(INITIAL_IDLE_DETECTION_ENABLED);
    private final BooleanProperty soundAlertsEnabled = new SimpleBooleanProperty(INITIAL_SOUND_ALERTS_ENABLED);
    private final BooleanProperty launchAtStartupEnabled = new SimpleBooleanProperty(INITIAL_LAUNCH_AT_STARTUP_ENABLED);
    private final StringProperty errorMessage = new SimpleStringProperty(INITIAL_ERROR_MESSAGE);

    /**
     * Constructs the SettingsWindowViewModel and logs initialization.
     */
    public SettingsWindowViewModel() {
        LOGGER.debug("SettingsWindowViewModel initialized with default placeholder values");
    }

    /**
     * Gets the reminder interval minutes property.
     *
     * @return the reminder interval property
     */
    public IntegerProperty reminderIntervalMinutesProperty() {
        return reminderIntervalMinutes;
    }

    /**
     * Gets the current value of the reminder interval in minutes.
     *
     * @return the reminder interval in minutes
     */
    public int getReminderIntervalMinutes() {
        return reminderIntervalMinutes.get();
    }

    /**
     * Gets the break duration seconds property.
     *
     * @return the break duration property
     */
    public IntegerProperty breakDurationSecondsProperty() {
        return breakDurationSeconds;
    }

    /**
     * Gets the current value of the break duration in seconds.
     *
     * @return the break duration in seconds
     */
    public int getBreakDurationSeconds() {
        return breakDurationSeconds.get();
    }

    /**
     * Gets the snooze duration string property.
     *
     * @return the snooze duration property
     */
    public StringProperty snoozeDurationProperty() {
        return snoozeDuration;
    }

    /**
     * Gets the current value of the snooze duration description.
     *
     * @return the snooze duration
     */
    public String getSnoozeDuration() {
        return snoozeDuration.get();
    }

    /**
     * Gets the working hours enabled state property.
     *
     * @return the working hours enabled property
     */
    public BooleanProperty workingHoursEnabledProperty() {
        return workingHoursEnabled;
    }

    /**
     * Checks if working hours restriction is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isWorkingHoursEnabled() {
        return workingHoursEnabled.get();
    }

    /**
     * Gets the work start time string property.
     *
     * @return the work start time property
     */
    public StringProperty workStartTimeProperty() {
        return workStartTime;
    }

    /**
     * Gets the current value of the work start time.
     *
     * @return the work start time
     */
    public String getWorkStartTime() {
        return workStartTime.get();
    }

    /**
     * Gets the work end time string property.
     *
     * @return the work end time property
     */
    public StringProperty workEndTimeProperty() {
        return workEndTime;
    }

    /**
     * Gets the current value of the work end time.
     *
     * @return the work end time
     */
    public String getWorkEndTime() {
        return workEndTime.get();
    }

    /**
     * Gets the weekend reminders enabled state property.
     *
     * @return the weekend reminders enabled property
     */
    public BooleanProperty weekendRemindersEnabledProperty() {
        return weekendRemindersEnabled;
    }

    /**
     * Checks if reminders are enabled on weekends.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isWeekendRemindersEnabled() {
        return weekendRemindersEnabled.get();
    }

    /**
     * Gets the fullscreen pause enabled state property.
     *
     * @return the fullscreen pause enabled property
     */
    public BooleanProperty fullscreenPauseEnabledProperty() {
        return fullscreenPauseEnabled;
    }

    /**
     * Checks if pausing during fullscreen is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isFullscreenPauseEnabled() {
        return fullscreenPauseEnabled.get();
    }

    /**
     * Gets the idle detection enabled state property.
     *
     * @return the idle detection enabled property
     */
    public BooleanProperty idleDetectionEnabledProperty() {
        return idleDetectionEnabled;
    }

    /**
     * Checks if idle detection is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isIdleDetectionEnabled() {
        return idleDetectionEnabled.get();
    }

    /**
     * Gets the sound alerts enabled state property.
     *
     * @return the sound alerts enabled property
     */
    public BooleanProperty soundAlertsEnabledProperty() {
        return soundAlertsEnabled;
    }

    /**
     * Checks if sound alerts are enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isSoundAlertsEnabled() {
        return soundAlertsEnabled.get();
    }

    /**
     * Gets the launch at startup enabled state property.
     *
     * @return the launch at startup enabled property
     */
    public BooleanProperty launchAtStartupEnabledProperty() {
        return launchAtStartupEnabled;
    }

    /**
     * Checks if launch at startup is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isLaunchAtStartupEnabled() {
        return launchAtStartupEnabled.get();
    }

    /**
     * Gets the error message string property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the current value of the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }
}
