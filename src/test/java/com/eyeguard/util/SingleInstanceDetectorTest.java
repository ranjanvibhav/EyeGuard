package com.eyeguard.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for SingleInstanceDetector class.
 */
class SingleInstanceDetectorTest {

    @BeforeEach
    void setUp() {
        SingleInstanceDetector.shutdown();
    }

    @AfterEach
    void tearDown() {
        SingleInstanceDetector.shutdown();
    }

    @Test
    void testFirstInstanceRegistration() {
        boolean isFirst = SingleInstanceDetector.checkAndRegister(() -> {});
        assertTrue(isFirst, "First instance registration should succeed");
    }

    @Test
    void testSecondInstanceFailsAndTriggersCallback() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        boolean isFirst = SingleInstanceDetector.checkAndRegister(latch::countDown);
        assertTrue(isFirst, "First instance should register");

        boolean isSecond = SingleInstanceDetector.checkAndRegister(() -> {});
        assertFalse(isSecond, "Second instance registration should fail");

        boolean callbackFired = latch.await(3, TimeUnit.SECONDS);
        assertTrue(callbackFired, "First instance callback should be triggered by second instance");
    }

    @Test
    void testPortReleasedAfterShutdown() {
        boolean isFirst = SingleInstanceDetector.checkAndRegister(() -> {});
        assertTrue(isFirst, "First instance should register");

        SingleInstanceDetector.shutdown();

        boolean isSecond = SingleInstanceDetector.checkAndRegister(() -> {});
        assertTrue(isSecond, "Registration after shutdown should succeed");
    }
}
