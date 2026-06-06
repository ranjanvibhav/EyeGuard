package com.eyeguard.service;

import java.awt.EventQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TrayServiceImpl} verifying its interaction with SystemTray.
 */
class TrayServiceImplTest {

    private AtomicBoolean openWindowCalled;
    private AtomicBoolean openDashboardCalled;
    private AtomicBoolean snoozeCalled;
    private AtomicBoolean pauseResumeCalled;
    private AtomicBoolean openSettingsCalled;
    private AtomicBoolean quitCalled;
    private TrayServiceImpl trayService;

    /**
     * Set up mock callbacks and the TrayServiceImpl before each test.
     */
    @BeforeEach
    void setUp() {
        openWindowCalled = new AtomicBoolean(false);
        openDashboardCalled = new AtomicBoolean(false);
        snoozeCalled = new AtomicBoolean(false);
        pauseResumeCalled = new AtomicBoolean(false);
        openSettingsCalled = new AtomicBoolean(false);
        quitCalled = new AtomicBoolean(false);
        trayService = new TrayServiceImpl(
            () -> openWindowCalled.set(true),
            () -> openDashboardCalled.set(true),
            () -> snoozeCalled.set(true),
            () -> pauseResumeCalled.set(true),
            () -> openSettingsCalled.set(true),
            () -> quitCalled.set(true)
        );
    }

    /**
     * Verifies that initializing tray runs without exception on the AWT thread.
     *
     * @throws Exception if latch await fails
     */
    @Test
    void testInitializeTray() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            assertDoesNotThrow(() -> trayService.initializeTray());
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Verifies that updating the tooltip runs without exception.
     *
     * @throws Exception if latch await fails
     */
    @Test
    void testUpdateTooltip() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            assertDoesNotThrow(() -> trayService.updateTooltip("New Tooltip"));
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Verifies that updating the status menu item runs without exception.
     *
     * @throws Exception if latch await fails
     */
    @Test
    void testUpdateStatusMenuItem() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            assertDoesNotThrow(() -> trayService.updateStatusMenuItem("New Status"));
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Verifies that updating the pause menu item runs without exception.
     *
     * @throws Exception if latch await fails
     */
    @Test
    void testUpdatePauseMenuItem() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            assertDoesNotThrow(() -> trayService.updatePauseMenuItem("New Pause Label"));
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Verifies that disposing the tray runs without exception.
     *
     * @throws Exception if latch await fails
     */
    @Test
    void testDispose() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            assertDoesNotThrow(() -> trayService.dispose());
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
