package com.eyeguard.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for MacOsStartupService.
 */
class MacOsStartupServiceTest {

    private MacOsStartupService service;

    @BeforeEach
    void setUp() {
        service = new MacOsStartupService();
    }

    @AfterEach
    void tearDown() {
        service.unregister();
    }

    @Test
    void testRegisterUnregister() {
        service.register();
        assertTrue(service.isRegistered());
        service.unregister();
        assertFalse(service.isRegistered());
    }
}
