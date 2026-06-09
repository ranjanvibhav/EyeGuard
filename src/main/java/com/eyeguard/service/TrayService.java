package com.eyeguard.service;

/**
 * Service interface for managing the system tray lifecycle, tooltips, and context menu labels.
 */
public interface TrayService {

    /**
     * Initializes the system tray icon, context menu items, and ActionListeners.
     * Must be called on the AWT Event Dispatch Thread.
     */
    void initializeTray();

    /**
     * Updates the tooltip text shown when hovering over the tray icon.
     * Must be called on the AWT Event Dispatch Thread.
     *
     * @param text the new tooltip text
     */
    void updateTooltip(String text);

    /**
     * Updates the status menu item text inside the context menu.
     * Must be called on the AWT Event Dispatch Thread.
     *
     * @param text the new status label text
     */
    void updateStatusMenuItem(String text);

    /**
     * Updates the pause/resume menu item text inside the context menu.
     * Must be called on the AWT Event Dispatch Thread.
     *
     * @param text the new pause/resume label text
     */
    void updatePauseMenuItem(String text);

    /**
     * Removes the tray icon from the system tray and releases resources.
     * Must be called on the AWT Event Dispatch Thread.
     */
    void dispose();

    /**
     * Toggles the system tray icon to show orange warning color or normal teal.
     * Must be called on the AWT Event Dispatch Thread.
     *
     * @param warning true to show warning mode, false to show normal mode
     */
    void setWarningMode(boolean warning);
}
