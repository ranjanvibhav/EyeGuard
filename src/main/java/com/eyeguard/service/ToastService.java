package com.eyeguard.service;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Service to manage display and dismissal of non-intrusive toast notifications.
 */
public interface ToastService {

    /**
     * Shows a toast notification containing the message for a set duration.
     * Must be called on JavaFX Application Thread.
     *
     * @param message         text to display
     * @param durationSeconds display duration in seconds
     */
    void showToast(String message, int durationSeconds);

    /**
     * Immediately dismisses and hides the toast stage.
     * Must be called on JavaFX Application Thread.
     */
    void hideToast();

    /**
     * Gets the observable toast visibility property.
     *
     * @return read-only property indicating if toast is active
     */
    ReadOnlyBooleanProperty toastVisibleProperty();
}
