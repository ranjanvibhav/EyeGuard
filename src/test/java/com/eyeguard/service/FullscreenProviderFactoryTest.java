package com.eyeguard.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link FullscreenProviderFactory}.
 */
class FullscreenProviderFactoryTest {

    @Test
    void testCreateReturnsNonNullProvider() {
        final SystemFullscreenProvider provider = FullscreenProviderFactory.create();
        assertNotNull(provider);
    }

    @Test
    void testCreateReturnsCorrectOSProvider() {
        final String osName = System.getProperty("os.name").toLowerCase();
        final SystemFullscreenProvider provider = FullscreenProviderFactory.create();
        if (osName.contains("win")) {
            assertTrue(provider instanceof WindowsFullscreenProvider);
        } else if (osName.contains("mac")) {
            assertTrue(provider instanceof MacOsFullscreenProvider);
        } else {
            assertNotNull(provider);
        }
    }

    @Test
    void testProviderDoesNotThrow() {
        final SystemFullscreenProvider provider = FullscreenProviderFactory.create();
        try {
            provider.isFullscreenWindowPresent();
        } catch (final Exception e) {
            org.junit.jupiter.api.Assertions.fail("isFullscreenWindowPresent threw an exception: " + e.getMessage());
        }
    }
}
