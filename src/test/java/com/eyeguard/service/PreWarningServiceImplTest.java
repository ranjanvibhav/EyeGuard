package com.eyeguard.service;

import com.eyeguard.model.TimerState;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PreWarningServiceImpl.
 */
class PreWarningServiceImplTest {

    private ToastService toastService;
    private TrayService trayService;
    private TimerService timerService;
    private PreWarningServiceImpl preWarningService;
    private SimpleDoubleProperty progressProp;
    private SimpleObjectProperty<TimerState> stateProp;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() {
        toastService = mock(ToastService.class);
        trayService = mock(TrayService.class);
        timerService = mock(TimerService.class);
        progressProp = new SimpleDoubleProperty(1.0);
        stateProp = new SimpleObjectProperty<>(TimerState.RUNNING);

        when(timerService.progressProperty()).thenReturn(progressProp);
        when(timerService.timerStateProperty()).thenReturn(stateProp);
        when(timerService.getTimerState()).thenReturn(TimerState.RUNNING);

        preWarningService = new PreWarningServiceImpl(toastService, trayService);
    }

    @Test
    void testAttachAndDetach() {
        preWarningService.attach(timerService);
        preWarningService.detach();
        when(timerService.getRemainingSeconds()).thenReturn(119);
        progressProp.set(0.5);
        verify(trayService, never()).setWarningMode(anyBoolean());
    }

    @Test
    void testWarningTriggered() throws InterruptedException {
        preWarningService.attach(timerService);
        when(timerService.getRemainingSeconds()).thenReturn(119);
        when(timerService.getTimerState()).thenReturn(TimerState.RUNNING);

        final CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(trayService).setWarningMode(true);

        progressProp.set(0.5);

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        verify(toastService).showToast(anyString(), anyInt());
        verify(trayService).setWarningMode(true);
    }

    @Test
    void testWarningNotTriggeredIfPaused() throws InterruptedException {
        preWarningService.attach(timerService);
        when(timerService.getRemainingSeconds()).thenReturn(119);
        when(timerService.getTimerState()).thenReturn(TimerState.PAUSED);

        progressProp.set(0.5);
        Thread.sleep(100);
        verify(toastService, never()).showToast(anyString(), anyInt());
        verify(trayService, never()).setWarningMode(anyBoolean());
    }

    @Test
    void testWarningResetState() {
        preWarningService.attach(timerService);
        stateProp.set(TimerState.BREAK_DUE);
        verify(trayService).setWarningMode(false);
    }

    @Test
    void testSetInvalidThreshold() {
        assertThrows(IllegalArgumentException.class, () -> preWarningService.setWarningThresholdSeconds(0));
        assertThrows(IllegalArgumentException.class, () -> preWarningService.setWarningThresholdSeconds(-10));
    }
}
