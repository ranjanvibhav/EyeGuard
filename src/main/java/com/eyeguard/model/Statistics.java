package com.eyeguard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model representing daily user statistics for EyeGuard.
 * Used for JSON serialization and deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Statistics {

    private int breaksTaken;
    private int snoozedCount;
    private int streakDays;
    private String lastActiveDate;

    /**
     * Constructs a Statistics instance with default values.
     */
    public Statistics() {
        this.breaksTaken = 0;
        this.snoozedCount = 0;
        this.streakDays = 0;
        this.lastActiveDate = "";
    }

    public int getBreaksTaken() {
        return breaksTaken;
    }

    public void setBreaksTaken(final int breaksTaken) {
        this.breaksTaken = breaksTaken;
    }

    public int getSnoozedCount() {
        return snoozedCount;
    }

    public void setSnoozedCount(final int snoozedCount) {
        this.snoozedCount = snoozedCount;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(final int streakDays) {
        this.streakDays = streakDays;
    }

    public String getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(final String lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
}
