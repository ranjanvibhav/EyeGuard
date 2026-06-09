package com.eyeguard.service;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Service to detect if any application is running in fullscreen mode.
 */
public interface FullscreenDetectionService {

    /**
     * Starts polling for fullscreen windows. Does nothing if already started.
     */
    void start();

    /**
     * Stops polling and releases scheduler resources.
     */
    void stop();

    /**
     * Sets callback to fire when entering fullscreen mode.
     * Called on JavaFX Application Thread.
     *
     * @param callback the action to run
     */
    void setOnFullscreenEntered(Runnable callback);

    /**
     * Sets callback to fire when exiting fullscreen mode.
     * Called on JavaFX Application Thread.
     *
     * @param callback the action to run
     */
    void setOnFullscreenExited(Runnable callback);

    /**
     * Gets the observable fullscreen active property.
     *
     * @return read-only property indicating if fullscreen mode is active
     */
    ReadOnlyBooleanProperty isFullscreenActiveProperty();

    /**
     * Returns whether fullscreen mode is currently active.
     *
     * @return true if fullscreen, false otherwise
     */
    boolean isFullscreenActive();
}
