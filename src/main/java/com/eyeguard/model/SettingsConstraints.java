package com.eyeguard.model;

/**
 * Constants defining the range constraints and defaults for EyeGuard settings.
 */
public final class SettingsConstraints {

    /**
     * Minimum allowed reminder interval in minutes.
     */
    public static final int MIN_REMINDER_INTERVAL_MINUTES = 5;

    /**
     * Maximum allowed reminder interval in minutes.
     */
    public static final int MAX_REMINDER_INTERVAL_MINUTES = 60;

    /**
     * Default reminder interval in minutes.
     */
    public static final int DEFAULT_REMINDER_INTERVAL = 20;

    /**
     * Minimum allowed break duration in seconds.
     */
    public static final int MIN_BREAK_DURATION_SECONDS = 10;

    /**
     * Maximum allowed break duration in seconds.
     */
    public static final int MAX_BREAK_DURATION_SECONDS = 60;

    /**
     * Default break duration in seconds.
     */
    public static final int DEFAULT_BREAK_DURATION = 20;

    /**
     * Minimum allowed snooze duration in minutes.
     */
    public static final int MIN_SNOOZE_DURATION_MINUTES = 5;

    /**
     * Maximum allowed snooze duration in minutes.
     */
    public static final int MAX_SNOOZE_DURATION_MINUTES = 30;

    /**
     * Default snooze duration in minutes.
     */
    public static final int DEFAULT_SNOOZE_DURATION = 5;

    /**
     * Default work start time in HH:mm format.
     */
    public static final String DEFAULT_WORK_START_TIME = "09:00";

    /**
     * Default work end time in HH:mm format.
     */
    public static final String DEFAULT_WORK_END_TIME = "19:00";

    /**
     * Name of the directory where settings are stored.
     */
    public static final String SETTINGS_DIRECTORY_NAME = ".eyeguard";

    /**
     * File name of the configuration settings.
     */
    public static final String SETTINGS_FILE_NAME = "settings.json";

    private SettingsConstraints() {
        // Prevent instantiation
    }
}
