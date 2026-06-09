package com.eyeguard.service;

/**
 * Service to register/unregister the application to launch at OS startup.
 */
public interface StartupService {

    /**
     * Registers the application to launch at OS startup.
     */
    void register();

    /**
     * Unregisters the application from launch at OS startup.
     */
    void unregister();

    /**
     * Checks if the application is registered to launch at OS startup.
     *
     * @return true if registered, false otherwise
     */
    boolean isRegistered();
}
