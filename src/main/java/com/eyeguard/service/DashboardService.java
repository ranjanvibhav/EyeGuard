package com.eyeguard.service;

/**
 * Service interface for managing the dashboard popup stage lifecycle.
 */
public interface DashboardService {

    /**
     * Shows the dashboard popup on the primary screen.
     * Must be called on the JavaFX Application Thread.
     */
    void showDashboard();

    /**
     * Hides the dashboard popup from the screen.
     * Must run on the JavaFX Application Thread.
     */
    void hideDashboard();
}
