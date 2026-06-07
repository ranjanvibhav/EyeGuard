package com.eyeguard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating OS-appropriate instances of SystemIdleProvider.
 */
public final class IdleProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdleProviderFactory.class);

    private IdleProviderFactory() {
        // Prevent instantiation
    }

    /**
     * Creates and returns a SystemIdleProvider implementation based on the current operating system.
     *
     * @return the appropriate SystemIdleProvider
     */
    public static SystemIdleProvider create() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new WindowsIdleProvider();
        }
        if (osName.contains("mac")) {
            return new MacOsIdleProvider();
        }
        LOGGER.warn("Unsupported OS for idle detection: {}", osName);
        return () -> 0L;
    }
}
