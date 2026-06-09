package com.eyeguard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * Model representing the EyeGuard application configuration settings.
 * Designed for JSON serialization and deserialization via Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Settings {

    private int reminderIntervalMinutes;
    private int breakDurationSeconds;
    private int snoozeDurationMinutes;
    private boolean workingHoursEnabled;
    private String workStartTime;
    private String workEndTime;
    private boolean weekendRemindersEnabled;
    private boolean fullscreenPauseEnabled;
    private boolean idleDetectionEnabled;
    private boolean soundAlertsEnabled;
    private boolean launchAtStartupEnabled;

    /**
     * Constructs a Settings instance with default configuration values.
     */
    public Settings() {
        this.reminderIntervalMinutes = 20;
        this.breakDurationSeconds = 20;
        this.snoozeDurationMinutes = 5;
        this.workingHoursEnabled = true;
        this.workStartTime = "09:00";
        this.workEndTime = "19:00";
        this.weekendRemindersEnabled = false;
        this.fullscreenPauseEnabled = true;
        this.idleDetectionEnabled = true;
        this.soundAlertsEnabled = true;
        this.launchAtStartupEnabled = false;
    }

    /**
     * Constructs a Settings instance with the specified values.
     *
     * @param reminderIntervalMinutes   time interval between breaks in minutes
     * @param breakDurationSeconds      length of each break in seconds
     * @param snoozeDurationMinutes     snooze duration in minutes
     * @param workingHoursEnabled       whether reminders are restricted to working hours
     * @param workStartTime             start of working hours (HH:mm)
     * @param workEndTime               end of working hours (HH:mm)
     * @param weekendRemindersEnabled   whether reminders are enabled on weekends
     * @param fullscreenPauseEnabled    whether breaks pause during fullscreen mode
     * @param idleDetectionEnabled      whether breaks pause when user is idle
     * @param soundAlertsEnabled        whether sound chimes are enabled before breaks
     * @param launchAtStartupEnabled    whether the app launches on system login
     */
    public Settings(final int reminderIntervalMinutes,
                    final int breakDurationSeconds,
                    final int snoozeDurationMinutes,
                    final boolean workingHoursEnabled,
                    final String workStartTime,
                    final String workEndTime,
                    final boolean weekendRemindersEnabled,
                    final boolean fullscreenPauseEnabled,
                    final boolean idleDetectionEnabled,
                    final boolean soundAlertsEnabled,
                    final boolean launchAtStartupEnabled) {
        this.reminderIntervalMinutes = reminderIntervalMinutes;
        this.breakDurationSeconds = breakDurationSeconds;
        this.snoozeDurationMinutes = snoozeDurationMinutes;
        this.workingHoursEnabled = workingHoursEnabled;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.weekendRemindersEnabled = weekendRemindersEnabled;
        this.fullscreenPauseEnabled = fullscreenPauseEnabled;
        this.idleDetectionEnabled = idleDetectionEnabled;
        this.soundAlertsEnabled = soundAlertsEnabled;
        this.launchAtStartupEnabled = launchAtStartupEnabled;
    }

    /**
     * Gets the reminder interval in minutes.
     *
     * @return the reminder interval
     */
    public int getReminderIntervalMinutes() {
        return reminderIntervalMinutes;
    }

    /**
     * Sets the reminder interval in minutes.
     *
     * @param reminderIntervalMinutes the new interval
     */
    public void setReminderIntervalMinutes(final int reminderIntervalMinutes) {
        this.reminderIntervalMinutes = reminderIntervalMinutes;
    }

    /**
     * Gets the break duration in seconds.
     *
     * @return the break duration
     */
    public int getBreakDurationSeconds() {
        return breakDurationSeconds;
    }

    /**
     * Sets the break duration in seconds.
     *
     * @param breakDurationSeconds the new duration
     */
    public void setBreakDurationSeconds(final int breakDurationSeconds) {
        this.breakDurationSeconds = breakDurationSeconds;
    }

    /**
     * Gets the snooze duration in minutes.
     *
     * @return the snooze duration
     */
    public int getSnoozeDurationMinutes() {
        return snoozeDurationMinutes;
    }

    /**
     * Sets the snooze duration in minutes.
     *
     * @param snoozeDurationMinutes the new snooze duration
     */
    public void setSnoozeDurationMinutes(final int snoozeDurationMinutes) {
        this.snoozeDurationMinutes = snoozeDurationMinutes;
    }

    /**
     * Checks if working hours restriction is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isWorkingHoursEnabled() {
        return workingHoursEnabled;
    }

    /**
     * Sets whether working hours restriction is enabled.
     *
     * @param workingHoursEnabled the state
     */
    public void setWorkingHoursEnabled(final boolean workingHoursEnabled) {
        this.workingHoursEnabled = workingHoursEnabled;
    }

    /**
     * Gets the working hours start time.
     *
     * @return the start time string (HH:mm)
     */
    public String getWorkStartTime() {
        return workStartTime;
    }

    /**
     * Sets the working hours start time.
     *
     * @param workStartTime the start time (HH:mm)
     */
    public void setWorkStartTime(final String workStartTime) {
        this.workStartTime = workStartTime;
    }

    /**
     * Gets the working hours end time.
     *
     * @return the end time string (HH:mm)
     */
    public String getWorkEndTime() {
        return workEndTime;
    }

    /**
     * Sets the working hours end time.
     *
     * @param workEndTime the end time (HH:mm)
     */
    public void setWorkEndTime(final String workEndTime) {
        this.workEndTime = workEndTime;
    }

    /**
     * Checks if weekend reminders are enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isWeekendRemindersEnabled() {
        return weekendRemindersEnabled;
    }

    /**
     * Sets whether weekend reminders are enabled.
     *
     * @param weekendRemindersEnabled the state
     */
    public void setWeekendRemindersEnabled(final boolean weekendRemindersEnabled) {
        this.weekendRemindersEnabled = weekendRemindersEnabled;
    }

    /**
     * Checks if fullscreen pause is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isFullscreenPauseEnabled() {
        return fullscreenPauseEnabled;
    }

    /**
     * Sets whether fullscreen pause is enabled.
     *
     * @param fullscreenPauseEnabled the state
     */
    public void setFullscreenPauseEnabled(final boolean fullscreenPauseEnabled) {
        this.fullscreenPauseEnabled = fullscreenPauseEnabled;
    }

    /**
     * Checks if idle detection is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isIdleDetectionEnabled() {
        return idleDetectionEnabled;
    }

    /**
     * Sets whether idle detection is enabled.
     *
     * @param idleDetectionEnabled the state
     */
    public void setIdleDetectionEnabled(final boolean idleDetectionEnabled) {
        this.idleDetectionEnabled = idleDetectionEnabled;
    }

    /**
     * Checks if sound alerts are enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isSoundAlertsEnabled() {
        return soundAlertsEnabled;
    }

    /**
     * Sets whether sound alerts are enabled.
     *
     * @param soundAlertsEnabled the state
     */
    public void setSoundAlertsEnabled(final boolean soundAlertsEnabled) {
        this.soundAlertsEnabled = soundAlertsEnabled;
    }

    /**
     * Checks if launch at startup is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isLaunchAtStartupEnabled() {
        return launchAtStartupEnabled;
    }

    /**
     * Sets whether launch at startup is enabled.
     *
     * @param launchAtStartupEnabled the state
     */
    public void setLaunchAtStartupEnabled(final boolean launchAtStartupEnabled) {
        this.launchAtStartupEnabled = launchAtStartupEnabled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Settings settings = (Settings) o;
        return reminderIntervalMinutes == settings.reminderIntervalMinutes
                && breakDurationSeconds == settings.breakDurationSeconds
                && snoozeDurationMinutes == settings.snoozeDurationMinutes
                && workingHoursEnabled == settings.workingHoursEnabled
                && weekendRemindersEnabled == settings.weekendRemindersEnabled
                && fullscreenPauseEnabled == settings.fullscreenPauseEnabled
                && idleDetectionEnabled == settings.idleDetectionEnabled
                && soundAlertsEnabled == settings.soundAlertsEnabled
                && launchAtStartupEnabled == settings.launchAtStartupEnabled
                && Objects.equals(workStartTime, settings.workStartTime)
                && Objects.equals(workEndTime, settings.workEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reminderIntervalMinutes, breakDurationSeconds, snoozeDurationMinutes,
                workingHoursEnabled, workStartTime, workEndTime, weekendRemindersEnabled,
                fullscreenPauseEnabled, idleDetectionEnabled, soundAlertsEnabled, launchAtStartupEnabled);
    }

    @Override
    public String toString() {
        return "Settings{"
                + "reminderIntervalMinutes=" + reminderIntervalMinutes
                + ", breakDurationSeconds=" + breakDurationSeconds
                + ", snoozeDurationMinutes=" + snoozeDurationMinutes
                + ", workingHoursEnabled=" + workingHoursEnabled
                + ", workStartTime='" + workStartTime + '\''
                + ", workEndTime='" + workEndTime + '\''
                + ", weekendRemindersEnabled=" + weekendRemindersEnabled
                + ", fullscreenPauseEnabled=" + fullscreenPauseEnabled
                + ", idleDetectionEnabled=" + idleDetectionEnabled
                + ", soundAlertsEnabled=" + soundAlertsEnabled
                + ", launchAtStartupEnabled=" + launchAtStartupEnabled
                + '}';
    }
}
