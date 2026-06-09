package com.eyeguard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to instantiate the platform-appropriate StartupService.
 */
public final class StartupServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(StartupServiceFactory.class);

    private StartupServiceFactory() {
        // Prevent instantiation
    }

    /**
     * Creates and returns the OS-specific StartupService implementation.
     *
     * @return StartupService implementation
     */
    public static StartupService create() {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            log.info("Detected Windows OS; creating WindowsStartupService");
            return new WindowsStartupService();
        } else if (os.contains("mac")) {
            log.info("Detected macOS OS; creating MacOsStartupService");
            return new MacOsStartupService();
        }
        log.warn("Unsupported OS: {}; returning NoOpStartupService", os);
        return new NoOpStartupService();
    }

    /**
     * Fallback no-op StartupService implementation for unsupported platforms.
     */
    private static class NoOpStartupService implements StartupService {
        @Override
        public void register() {
            log.info("No-op startup register called");
        }

        @Override
        public void unregister() {
            log.info("No-op startup unregister called");
        }

        @Override
        public boolean isRegistered() {
            return false;
        }
    }
}
