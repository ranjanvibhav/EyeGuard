package com.eyeguard.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link StartupServiceFactory} verifying correct OS mapping.
 */
class StartupServiceFactoryTest {

    private String originalOs;

    @BeforeEach
    void setUp() {
        originalOs = System.getProperty("os.name");
    }

    @AfterEach
    void tearDown() {
        System.setProperty("os.name", originalOs);
    }

    @Test
    void testWindowsOS() {
        System.setProperty("os.name", "Windows 11");
        final StartupService service = StartupServiceFactory.create();
        assertTrue(service instanceof WindowsStartupService);
    }

    @Test
    void testMacOs() {
        System.setProperty("os.name", "Mac OS X");
        final StartupService service = StartupServiceFactory.create();
        assertTrue(service instanceof MacOsStartupService);
    }

    @Test
    void testLinuxOS() {
        System.setProperty("os.name", "Linux");
        final StartupService service = StartupServiceFactory.create();
        assertTrue(service.getClass().getSimpleName().contains("NoOpStartupService"));
        service.register();
        service.unregister();
        assertFalse(service.isRegistered());
    }
}
