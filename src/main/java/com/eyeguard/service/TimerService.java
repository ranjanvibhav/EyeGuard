package com.eyeguard.service;

import com.eyeguard.model.TimerState;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 * Service interface for managing the core countdown timer lifecycle and state.
 */
public interface TimerService {

    /**
     * Starts a fresh countdown from the specified total seconds.
     *
     * @param totalSeconds the countdown duration in seconds
     * @throws IllegalArgumentException if totalSeconds is less than or equal to 0
     * @throws IllegalStateException if the timer is already in RUNNING state
     */
    void start(int totalSeconds);

    /**
     * Pauses the active countdown. Only valid when the timer is in RUNNING state.
     *
     * @throws IllegalStateException if the timer is not in RUNNING state
     */
    void pause();

    /**
     * Resumes the paused countdown. Only valid when the timer is in PAUSED state.
     *
     * @throws IllegalStateException if the timer is not in PAUSED state
     */
    void resume();

    /**
     * Resets the countdown by stopping the current timer and starting fresh with the specified duration.
     *
     * @param totalSeconds the new countdown duration in seconds
     */
    void reset(int totalSeconds);

    /**
     * Stops the timer completely and cleans up background scheduler resources.
     */
    void stop();

    /**
     * Registers a callback action to execute when the countdown reaches zero.
     * The callback is guaranteed to be executed on the JavaFX Application Thread.
     *
     * @param callback the action to run when the break is due
     */
    void setOnBreakDue(Runnable callback);

    /**
     * Gets the read-only countdown text property formatted as "MM:SS".
     *
     * @return the countdown text property
     */
    ReadOnlyStringProperty countdownTextProperty();

    /**
     * Gets the read-only progress ratio property ranging from 1.0 (full) down to 0.0 (empty).
     *
     * @return the progress property
     */
    ReadOnlyDoubleProperty progressProperty();

    /**
     * Gets the read-only timer state property.
     *
     * @return the timer state property
     */
    ReadOnlyObjectProperty<TimerState> timerStateProperty();

    /**
     * Returns the current state of the timer synchronously.
     *
     * @return the current TimerState
     */
    TimerState getTimerState();

    /**
     * Returns the remaining seconds of the countdown synchronously.
     *
     * @return the remaining seconds
     */
    int getRemainingSeconds();
}
