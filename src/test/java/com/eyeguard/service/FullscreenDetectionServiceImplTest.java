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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for {@link FullscreenDetectionServiceImpl}.
 */
class FullscreenDetectionServiceImplTest {

    private static final int LATCH_TIMEOUT_SECONDS = 5;

    private StubFullscreenProvider stubProvider;
    private FullscreenDetectionServiceImpl service;

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
        stubProvider = new StubFullscreenProvider();
        service = new FullscreenDetectionServiceImpl(stubProvider);
    }

    @AfterEach
    void tearDown() {
        service.stop();
    }

    @Test
    void testInitialValue() {
        assertFalse(service.isFullscreenActive());
        assertFalse(service.isFullscreenActiveProperty().get());
    }

    @Test
    void testFullscreenTransitions() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger enteredCount = new AtomicInteger(0);
        final AtomicInteger exitedCount = new AtomicInteger(0);

        service.setOnFullscreenEntered(enteredCount::incrementAndGet);
        service.setOnFullscreenExited(exitedCount::incrementAndGet);
        service.start();

        stubProvider.setFullscreen(true);
        Platform.runLater(() -> {
            try {
                service.poll();
                assertTrue(service.isFullscreenActive());
                assertEquals(1, enteredCount.get());
                stubProvider.setFullscreen(false);
                service.poll();
                assertFalse(service.isFullscreenActive());
                assertEquals(1, exitedCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testNoDuplicateCallbacks() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger enteredCount = new AtomicInteger(0);
        service.setOnFullscreenEntered(enteredCount::incrementAndGet);
        service.start();

        stubProvider.setFullscreen(true);
        Platform.runLater(() -> {
            try {
                service.poll();
                service.poll();
                assertEquals(1, enteredCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testStopPreventsCallbacks() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger enteredCount = new AtomicInteger(0);
        service.setOnFullscreenEntered(enteredCount::incrementAndGet);
        service.start();
        service.stop();

        stubProvider.setFullscreen(true);
        Platform.runLater(() -> {
            try {
                service.poll();
                assertFalse(service.isFullscreenActive());
                assertEquals(0, enteredCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testStartTwiceSafeguard() {
        service.start();
        service.start();
        assertFalse(service.isFullscreenActive());
    }

    private static class StubFullscreenProvider implements SystemFullscreenProvider {
        private boolean fullscreen = false;
        public void setFullscreen(final boolean f) { this.fullscreen = f; }
        @Override public boolean isFullscreenWindowPresent() { return fullscreen; }
    }
}
