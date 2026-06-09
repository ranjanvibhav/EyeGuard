package com.eyeguard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating SystemFullscreenProvider instances based on OS.
 */
public final class FullscreenProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullscreenProviderFactory.class);

    private FullscreenProviderFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a SystemFullscreenProvider for the current operating system.
     *
     * @return OS-specific provider or a stub always returning false
     */
    public static SystemFullscreenProvider create() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new WindowsFullscreenProvider();
        } else if (osName.contains("mac")) {
            return new MacOsFullscreenProvider();
        } else {
            LOGGER.warn("Unsupported OS for fullscreen detection: {}", osName);
            return () -> false;
        }
    }
}
