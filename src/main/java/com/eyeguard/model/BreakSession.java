package com.eyeguard.model;

import java.time.LocalDateTime;

/**
 * Model representing an individual break session tracking its duration and outcome.
 * Holds details on whether the break was completed normally or snoozed.
 */
public final class BreakSession {

    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;
    private boolean snoozed;
    private final int durationSeconds;

    /**
     * Constructs a new BreakSession with start time and duration.
     *
     * @param startTime       the start time of the break session
     * @param durationSeconds the configured duration of the break in seconds
     */
    public BreakSession(final LocalDateTime startTime, final int durationSeconds) {
        this.startTime = startTime;
        this.durationSeconds = durationSeconds;
        this.completed = false;
        this.snoozed = false;
        this.endTime = null;
    }

    /**
     * Marks the break session as completed successfully.
     *
     * @param endTime the time the break was completed
     * @throws IllegalStateException if the session is already finished
     */
    public void complete(final LocalDateTime endTime) {
        if (isFinished()) {
            throw new IllegalStateException("Break session already finished.");
        }
        this.endTime = endTime;
        this.completed = true;
    }

    /**
     * Marks the break session as snoozed.
     *
     * @param endTime the time the break was snoozed
     * @throws IllegalStateException if the session is already finished
     */
    public void snooze(final LocalDateTime endTime) {
        if (isFinished()) {
            throw new IllegalStateException("Break session already finished.");
        }
        this.endTime = endTime;
        this.snoozed = true;
    }

    /**
     * Gets the start time of this session.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of this session, or null if not finished.
     *
     * @return the end time, or null
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns whether the session was completed.
     *
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Returns whether the session was snoozed.
     *
     * @return true if snoozed, false otherwise
     */
    public boolean isSnoozed() {
        return snoozed;
    }

    /**
     * Gets the configured duration of the break session in seconds.
     *
     * @return the duration in seconds
     */
    public int getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * Checks if this session has finished (completed or snoozed).
     *
     * @return true if finished, false otherwise
     */
    public boolean isFinished() {
        return completed || snoozed;
    }

    @Override
    public String toString() {
        return "BreakSession{"
                + "startTime=" + startTime
                + ", endTime=" + endTime
                + ", completed=" + completed
                + ", snoozed=" + snoozed
                + ", durationSeconds=" + durationSeconds
                + '}';
    }
}
