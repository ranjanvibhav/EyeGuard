package com.eyeguard.service;

/**
 * Service interface for managing the visibility lifecycle of the full-screen break overlay.
 */
public interface OverlayService {

    /**
     * Displays the break overlay in full-screen mode on the primary monitor.
     * Must be called on the JavaFX Application Thread.
     */
    void showOverlay();

    /**
     * Dismisses the break overlay.
     * Must be called on the JavaFX Application Thread.
     */
    void hideOverlay();
}
