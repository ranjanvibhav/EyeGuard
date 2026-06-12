package com.eyeguard.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for {@link IdleDetectionServiceImpl}.
 */
class IdleDetectionServiceImplTest {

    private static final int LATCH_TIMEOUT_SECONDS = 5;

    private StubIdleProvider stubProvider;
    private IdleDetectionServiceImpl service;

    /**
     * Initializes the JavaFX Toolkit runtime required for properties testing.
     */
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Already initialized
        }
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() {
        stubProvider = new StubIdleProvider();
        service = new IdleDetectionServiceImpl(stubProvider);
    }

    @AfterEach
    void tearDown() {
        service.stop();
    }

    @Test
    void testInitialValue() {
        assertFalse(service.isIdle());
        assertFalse(service.isIdleProperty().get());
    }

    @Test
    void testIdleTransitions() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger idleCount = new AtomicInteger(0);
        final AtomicInteger activeCount = new AtomicInteger(0);

        service.setOnIdleDetected(idleCount::incrementAndGet);
        service.setOnActivityResumed(activeCount::incrementAndGet);
        service.start();

        stubProvider.setIdleSeconds(400);
        Platform.runLater(() -> {
            try {
                service.poll();
                assertTrue(service.isIdle());
                assertEquals(1, idleCount.get());
                stubProvider.setIdleSeconds(10);
                service.poll();
                assertFalse(service.isIdle());
                assertEquals(1, activeCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testNoDuplicateIdleCallback() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger idleCount = new AtomicInteger(0);
        service.setOnIdleDetected(idleCount::incrementAndGet);
        service.start();

        stubProvider.setIdleSeconds(400);
        Platform.runLater(() -> {
            try {
                service.poll();
                service.poll();
                assertEquals(1, idleCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testThresholdValidations() {
        assertThrows(IllegalArgumentException.class, () -> service.setIdleThresholdSeconds(0));
        assertThrows(IllegalArgumentException.class, () -> service.setIdleThresholdSeconds(-5));
        service.setIdleThresholdSeconds(100);
    }

    @Test
    void testStopPreventsCallbacks() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger idleCount = new AtomicInteger(0);
        service.setOnIdleDetected(idleCount::incrementAndGet);
        service.start();
        service.stop();

        stubProvider.setIdleSeconds(400);
        Platform.runLater(() -> {
            try {
                service.poll();
                assertFalse(service.isIdle());
                assertEquals(0, idleCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testScreenLockTransitions() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger idleCount = new AtomicInteger(0);
        final AtomicInteger activeCount = new AtomicInteger(0);
        service.setOnIdleDetected(idleCount::incrementAndGet);
        service.setOnActivityResumed(activeCount::incrementAndGet);
        service.start();
        Platform.runLater(() -> runLockChecks(idleCount, activeCount, latch));
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    private void runLockChecks(final AtomicInteger idle, final AtomicInteger active, final CountDownLatch latch) {
        service.setLocked(true);
        service.poll();
        assertTrue(service.isIdle());
        assertEquals(1, idle.get());
        service.setLocked(false);
        service.poll();
        assertFalse(service.isIdle());
        assertEquals(1, active.get());
        latch.countDown();
    }

    @Test
    void testOnScreenUnlockedCallbackAfterLockThenUnlock() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger activeCount = new AtomicInteger(0);
        service.setOnActivityResumed(activeCount::incrementAndGet);
        service.start();
        Platform.runLater(() -> {
            service.setLocked(true);
            service.poll();
            service.setLocked(false);
            service.poll();
            assertEquals(1, activeCount.get());
            latch.countDown();
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testOnActivityResumedCallbackAfterIdleThenActive() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger activeCount = new AtomicInteger(0);
        service.setOnActivityResumed(activeCount::incrementAndGet);
        service.start();
        Platform.runLater(() -> {
            stubProvider.setIdleSeconds(400);
            service.poll();
            stubProvider.setIdleSeconds(10);
            service.poll();
            assertEquals(1, activeCount.get());
            latch.countDown();
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    private static class StubIdleProvider implements SystemIdleProvider {
        private long idleSeconds = 0;
        public void setIdleSeconds(final long s) { this.idleSeconds = s; }
        @Override public long getIdleTimeSeconds() { return idleSeconds; }
    }
}
