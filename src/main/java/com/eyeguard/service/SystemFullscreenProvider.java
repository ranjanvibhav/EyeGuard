package com.eyeguard.service;

/**
 * Platform-independent provider interface to check if any fullscreen window is present.
 */
public interface SystemFullscreenProvider {

    /**
     * Checks if any window is covering the entire primary screen.
     *
     * @return true if a fullscreen window is present, false otherwise
     */
    boolean isFullscreenWindowPresent();
}
