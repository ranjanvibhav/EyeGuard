package com.eyeguard.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model representing the configured working hours.
 */
public final class WorkingHours {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkingHours.class);

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean enabled;
    private final boolean weekendRemindersEnabled;

    /**
     * Constructs a WorkingHours instance.
     *
     * @param startTime               the start of working hours
     * @param endTime                 the end of working hours
     * @param enabled                 whether working hours are enabled
     * @param weekendRemindersEnabled whether weekend reminders are enabled
     */
    public WorkingHours(final LocalTime startTime, final LocalTime endTime,
                        final boolean enabled, final boolean weekendRemindersEnabled) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.enabled = enabled;
        this.weekendRemindersEnabled = weekendRemindersEnabled;
    }

    /**
     * Checks if a specific date and time is within working hours.
     *
     * @param dateTime the date and time to check
     * @return true if within working hours, false otherwise
     */
    public boolean isWithinWorkingHours(final LocalDateTime dateTime) {
        if (!enabled) {
            return true;
        }
        final DayOfWeek day = dateTime.getDayOfWeek();
        if (!weekendRemindersEnabled && (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)) {
            return false;
        }
        final LocalTime time = dateTime.toLocalTime();
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    /**
     * Checks if the current date and time is within working hours.
     *
     * @return true if within working hours, false otherwise
     */
    public boolean isWithinWorkingHours() {
        return isWithinWorkingHours(LocalDateTime.now());
    }

    /**
     * Creates a WorkingHours instance from a Settings object.
     *
     * @param settings the application settings
     * @return a new WorkingHours instance
     */
    public static WorkingHours fromSettings(final Settings settings) {
        try {
            final LocalTime start = LocalTime.parse(settings.getWorkStartTime());
            final LocalTime end = LocalTime.parse(settings.getWorkEndTime());
            return new WorkingHours(start, end, settings.isWorkingHoursEnabled(),
                    settings.isWeekendRemindersEnabled());
        } catch (final Exception e) {
            LOGGER.warn("Failed to parse working hours from settings, using defaults", e);
            return new WorkingHours(LocalTime.of(9, 0), LocalTime.of(19, 0), true, false);
        }
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isWeekendRemindersEnabled() {
        return weekendRemindersEnabled;
    }

    @Override
    public String toString() {
        return "WorkingHours{"
                + "startTime=" + startTime
                + ", endTime=" + endTime
                + ", enabled=" + enabled
                + ", weekendRemindersEnabled=" + weekendRemindersEnabled
                + '}';
    }
}
