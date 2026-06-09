package com.eyeguard.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for WindowsStartupService.
 */
class WindowsStartupServiceTest {

    private WindowsStartupService service;

    @BeforeEach
    void setUp() {
        service = new WindowsStartupService();
    }

    @AfterEach
    void tearDown() {
        service.unregister();
    }

    @Test
    void testRegisterUnregister() {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            service.register();
            assertTrue(service.isRegistered());
            service.unregister();
            assertFalse(service.isRegistered());
        }
    }
}
