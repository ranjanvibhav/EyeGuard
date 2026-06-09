package com.eyeguard.service;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Service to enforce configured working hours restrictions.
 */
public interface WorkingHoursService {

    /**
     * Starts polling working hours state. Does nothing if already started.
     */
    void start();

    /**
     * Stops polling and releases scheduler resources.
     */
    void stop();

    /**
     * Reloads working hours configuration from settings.
     */
    void reloadSettings();

    /**
     * Checks if current time is within configured working hours.
     *
     * @return true if within working hours, false otherwise
     */
    boolean isWithinWorkingHours();

    /**
     * Gets the observable withinWorkingHours property.
     *
     * @return read-only property indicating if we are currently within working hours
     */
    ReadOnlyBooleanProperty withinWorkingHoursProperty();

    /**
     * Sets callback to fire when transitioning from outside to inside working hours.
     * Called on JavaFX Application Thread.
     *
     * @param callback the action to run
     */
    void setOnWorkingHoursStarted(Runnable callback);

    /**
     * Sets callback to fire when transitioning from inside to outside working hours.
     * Called on JavaFX Application Thread.
     *
     * @param callback the action to run
     */
    void setOnWorkingHoursEnded(Runnable callback);
}
