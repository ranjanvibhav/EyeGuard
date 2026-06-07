package com.eyeguard.service;

import com.eyeguard.model.DndState;
import com.eyeguard.model.Settings;
import com.eyeguard.model.TimerState;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DndServiceImpl} using Mockito to mock dependencies.
 */
@ExtendWith(MockitoExtension.class)
class DndServiceImplTest {

    @Mock
    private TimerService timerService;

    @Mock
    private ConfigurationService configurationService;

    private DndServiceImpl dndService;

    /**
     * Initializes the JavaFX Toolkit runtime required for properties/stage testing.
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
        dndService = new DndServiceImpl(timerService, configurationService);
    }

    @Test
    void testInitialState() {
        assertEquals(DndState.INACTIVE, dndService.getDndState());
        assertEquals("Reminders active", dndService.dndStatusTextProperty().get());
        assertEquals("", dndService.resumeTimeTextProperty().get());
    }

    @Test
    void testSnoozeValid() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                dndService.snooze(5);
                assertEquals(DndState.SNOOZED, dndService.getDndState());
                assertEquals("Snoozed — resumes in 05:00", dndService.dndStatusTextProperty().get());
                verify(timerService).reset(300);
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        dndService.shutdown();
    }

    @Test
    void testSnoozeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> dndService.snooze(0));
        assertThrows(IllegalArgumentException.class, () -> dndService.snooze(-1));
    }

    @Test
    void testPauseValid() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        when(timerService.getTimerState()).thenReturn(TimerState.RUNNING);
        Platform.runLater(() -> {
            try {
                dndService.pause();
                assertEquals(DndState.PAUSED, dndService.getDndState());
                assertEquals("Paused — click Resume to continue", dndService.dndStatusTextProperty().get());
                verify(timerService).pause();
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        dndService.shutdown();
    }

    @Test
    void testPauseInvalid() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                dndService.snooze(5);
                assertThrows(IllegalStateException.class, () -> dndService.pause());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        dndService.shutdown();
    }

    @Test
    void testResumeFromPaused() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Settings settings = new Settings();
        settings.setReminderIntervalMinutes(20);
        when(configurationService.loadSettings()).thenReturn(settings);
        when(timerService.getTimerState()).thenReturn(TimerState.RUNNING).thenReturn(TimerState.PAUSED);
        Platform.runLater(() -> {
            try {
                dndService.pause();
                dndService.resume();
                assertEquals(DndState.INACTIVE, dndService.getDndState());
                verify(timerService).resume();
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        dndService.shutdown();
    }

    @Test
    void testResumeInvalid() {
        assertThrows(IllegalStateException.class, () -> dndService.resume());
    }

    @Test
    void testEnableMeetingModeValid() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        when(timerService.getTimerState()).thenReturn(TimerState.RUNNING);
        Platform.runLater(() -> {
            try {
                dndService.enableMeetingMode(30);
                assertEquals(DndState.MEETING_MODE, dndService.getDndState());
                assertEquals("Meeting mode — resumes in 30:00", dndService.dndStatusTextProperty().get());
                verify(timerService).pause();
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        dndService.shutdown();
    }

    @Test
    void testEnableMeetingModeInvalid() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                assertThrows(IllegalArgumentException.class, () -> dndService.enableMeetingMode(0));
                dndService.enableMeetingMode(30);
                assertThrows(IllegalStateException.class, () -> dndService.enableMeetingMode(30));
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        dndService.shutdown();
    }
}
