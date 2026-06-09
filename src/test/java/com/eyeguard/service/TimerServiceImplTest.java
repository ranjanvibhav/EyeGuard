package com.eyeguard.service;

import com.eyeguard.model.TimerState;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TimerServiceImpl} verifying states, transitions, exceptions and callbacks.
 */
class TimerServiceImplTest {

    private TimerServiceImpl timerService;

    /**
     * Initializes JavaFX context for testing observable properties.
     */
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Toolkit already initialized, safe to ignore
        }
        Platform.setImplicitExit(false);
    }

    /**
     * Creates a new TimerServiceImpl instance before each test.
     */
    @BeforeEach
    void setUp() {
        timerService = new TimerServiceImpl();
    }

    /**
     * Shuts down the background scheduler resources after each test.
     */
    @AfterEach
    void tearDown() {
        timerService.stop();
    }

    /**
     * Verifies default initial state and property values.
     */
    @Test
    void testInitialState() {
        assertEquals(TimerState.IDLE, timerService.getTimerState());
        assertEquals("20:00", timerService.countdownTextProperty().get());
        assertEquals(1.0, timerService.progressProperty().get());
        assertEquals(0, timerService.getRemainingSeconds());
    }

    /**
     * Verifies starting and pausing transitions on the FX thread.
     *
     * @throws Throwable if test assertions fail on the FX thread
     */
    @Test
    void testStartAndPause() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];
        Platform.runLater(() -> {
            try {
                timerService.start(10);
                assertEquals(TimerState.RUNNING, timerService.getTimerState());
                assertEquals("00:10", timerService.countdownTextProperty().get());
                assertEquals(10, timerService.getRemainingSeconds());

                timerService.pause();
                assertEquals(TimerState.PAUSED, timerService.getTimerState());
            } catch (final Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        if (error[0] != null) {
            throw error[0];
        }
    }

    /**
     * Verifies resuming and resetting transitions on the FX thread.
     *
     * @throws Throwable if test assertions fail on the FX thread
     */
    @Test
    void testResumeAndReset() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];
        Platform.runLater(() -> {
            try {
                timerService.start(10);
                timerService.pause();

                timerService.resume();
                assertEquals(TimerState.RUNNING, timerService.getTimerState());

                timerService.reset(5);
                assertEquals(TimerState.RUNNING, timerService.getTimerState());
                assertEquals("00:05", timerService.countdownTextProperty().get());
            } catch (final Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        if (error[0] != null) {
            throw error[0];
        }
    }

    /**
     * Verifies stopping transitions on the FX thread.
     *
     * @throws Throwable if test assertions fail on the FX thread
     */
    @Test
    void testStop() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];
        Platform.runLater(() -> {
            try {
                timerService.start(10);
                timerService.stop();
                assertEquals(TimerState.STOPPED, timerService.getTimerState());
            } catch (final Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        if (error[0] != null) {
            throw error[0];
        }
    }

    /**
     * Verifies starting validation and throwing correct exceptions.
     */
    @Test
    void testStartValidation() {
        assertThrows(IllegalArgumentException.class, () -> timerService.start(0));
        assertThrows(IllegalArgumentException.class, () -> timerService.start(-1));
        timerService.start(10);
        assertThrows(IllegalStateException.class, () -> timerService.start(10));
    }

    /**
     * Verifies pausing validation when not running.
     */
    @Test
    void testPauseValidation() {
        assertThrows(IllegalStateException.class, () -> timerService.pause());
    }

    /**
     * Verifies resuming validation when not paused.
     */
    @Test
    void testResumeValidation() {
        assertThrows(IllegalStateException.class, () -> timerService.resume());
        timerService.start(10);
        assertThrows(IllegalStateException.class, () -> timerService.resume());
    }

    /**
     * Verifies callback is executed and state is BREAK_DUE when countdown reaches 0.
     *
     * @throws Throwable if test assertions fail on the FX thread
     */
    @Test
    void testTimerCompletionAndCallback() throws Throwable {
        final CountDownLatch completionLatch = new CountDownLatch(1);
        final AtomicBoolean callbackFired = new AtomicBoolean(false);

        Platform.runLater(() -> {
            timerService.setOnBreakDue(() -> {
                callbackFired.set(true);
                completionLatch.countDown();
            });
            timerService.start(1);
        });

        assertTrue(completionLatch.await(5, TimeUnit.SECONDS));
        assertTrue(callbackFired.get());

        final CountDownLatch assertLatch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];
        Platform.runLater(() -> {
            try {
                assertEquals(TimerState.BREAK_DUE, timerService.getTimerState());
                assertEquals("00:00", timerService.countdownTextProperty().get());
                assertEquals(0.0, timerService.progressProperty().get());
            } catch (final Throwable t) {
                error[0] = t;
            } finally {
                assertLatch.countDown();
            }
        });
        assertTrue(assertLatch.await(5, TimeUnit.SECONDS));
        if (error[0] != null) {
            throw error[0];
        }
    }
}
