package com.eyeguard.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link IdleProviderFactory}.
 */
class IdleProviderFactoryTest {

    @Test
    void testCreateReturnsNonNullProvider() {
        final SystemIdleProvider provider = IdleProviderFactory.create();
        assertNotNull(provider);
    }

    @Test
    void testCreateReturnsCorrectPlatformProvider() {
        final String osName = System.getProperty("os.name").toLowerCase();
        final SystemIdleProvider provider = IdleProviderFactory.create();
        if (osName.contains("win")) {
            assertInstanceOf(WindowsIdleProvider.class, provider);
        } else if (osName.contains("mac")) {
            assertInstanceOf(MacOsIdleProvider.class, provider);
        }
    }

    @Test
    void testGetIdleTimeSecondsDoesNotThrow() {
        final SystemIdleProvider provider = IdleProviderFactory.create();
        assertDoesNotThrow(() -> provider.getIdleTimeSeconds());
    }
}
