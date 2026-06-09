package com.eyeguard.service;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Service contract for monitoring system-wide user inactivity (idle state)
 * and triggering callbacks when transitioning between active and idle states.
 */
public interface IdleDetectionService {

    /**
     * Begins polling for user inactivity. Does nothing if already started.
     */
    void start();

    /**
     * Stops polling and releases scheduler resources.
     */
    void stop();

    /**
     * Sets the threshold duration in seconds that determines when the system is considered idle.
     *
     * @param seconds the threshold in seconds
     * @throws IllegalArgumentException if seconds <= 0
     */
    void setIdleThresholdSeconds(int seconds);

    /**
     * Sets the callback action to trigger when the system transitions from active to idle.
     * Guaranteed to execute on the JavaFX Application Thread.
     *
     * @param callback the action to execute
     */
    void setOnIdleDetected(Runnable callback);

    /**
     * Sets the callback action to trigger when the system transitions from idle to active.
     * Guaranteed to execute on the JavaFX Application Thread.
     *
     * @param callback the action to execute
     */
    void setOnActivityResumed(Runnable callback);

    /**
     * Gets the observable idle state property.
     *
     * @return the idle state property
     */
    ReadOnlyBooleanProperty isIdleProperty();

    /**
     * Returns the current idle state synchronously.
     *
     * @return true if currently idle, false otherwise
     */
    boolean isIdle();

    /**
     * Returns the current idle time in seconds.
     *
     * @return the idle duration in seconds
     */
    long getIdleSeconds();
}
