package com.eyeguard.service;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 * Service interface for managing the break countdown lifecycle.
 * Coordinates break state, countdown timer execution, and window overlay actions.
 */
public interface BreakService {

    /**
     * Triggers the break flow. Shows full screen overlay and starts the break countdown.
     * Must be called on JavaFX Application Thread.
     */
    void startBreak();

    /**
     * Completes the break. Closes the overlay, stops the countdown, and resets the main timer.
     * Must be called on JavaFX Application Thread.
     */
    void completeBreak();

    /**
     * Snoozes the break. Closes the overlay, stops the countdown, and resets the main timer to snooze duration.
     * Must be called on JavaFX Application Thread.
     */
    void snoozeBreak();

    /**
     * Gets the remaining seconds of the break countdown.
     *
     * @return the remaining seconds property
     */
    ReadOnlyIntegerProperty breakCountdownProperty();

    /**
     * Returns true while the break overlay is showing and countdown is active.
     *
     * @return the active state property
     */
    ReadOnlyBooleanProperty breakActiveProperty();
}
