package com.eyeguard.service;

import com.eyeguard.model.Settings;
import com.eyeguard.model.TimerState;
import com.eyeguard.viewmodel.BreakOverlayViewModel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for {@link BreakServiceImpl}.
 */
class BreakServiceImplTest {

    private static final int LATCH_TIMEOUT_SECONDS = 5;

    private StubTimerService timerService;
    private StubOverlayService overlayService;
    private BreakOverlayViewModel viewModel;
    private StubConfigurationService configService;
    private BreakServiceImpl breakService;

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
        timerService = new StubTimerService();
        overlayService = new StubOverlayService();
        viewModel = new BreakOverlayViewModel();
        configService = new StubConfigurationService();
        breakService = new BreakServiceImpl(timerService, overlayService, viewModel, configService);
    }

    @Test
    void testStartBreakOnNonFxThreadThrows() {
        assertThrows(IllegalStateException.class, () -> breakService.startBreak());
    }

    @Test
    void testCompleteBreakOnNonFxThreadThrows() {
        assertThrows(IllegalStateException.class, () -> breakService.completeBreak());
    }

    @Test
    void testSnoozeBreakOnNonFxThreadThrows() {
        assertThrows(IllegalStateException.class, () -> breakService.snoozeBreak());
    }

    @Test
    void testStartAndCompleteBreak() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                breakService.startBreak();
                assertTrue(breakService.breakActiveProperty().get());
                assertTrue(overlayService.isShown());
                assertEquals(10, breakService.breakCountdownProperty().get());

                breakService.completeBreak();
                assertFalse(breakService.breakActiveProperty().get());
                assertFalse(overlayService.isShown());
                assertEquals(600, timerService.getLastResetSeconds());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        breakService.shutdown();
    }

    @Test
    void testSnoozeBreak() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                breakService.startBreak();
                breakService.snoozeBreak();
                assertFalse(breakService.breakActiveProperty().get());
                assertFalse(overlayService.isShown());
                assertEquals(180, timerService.getLastResetSeconds());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        breakService.shutdown();
    }

    @Test
    void testCountdownDecrement() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                breakService.startBreak();
            } catch (final Exception e) {
                fail(e);
            }
        });
        Thread.sleep(1500);
        Platform.runLater(() -> {
            try {
                assertTrue(breakService.breakCountdownProperty().get() < 10);
                breakService.completeBreak();
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        breakService.shutdown();
    }

    private static class StubTimerService implements TimerService {
        private int lastResetSeconds = -1;
        @Override
        public void reset(int totalSeconds) { this.lastResetSeconds = totalSeconds; }
        public int getLastResetSeconds() { return lastResetSeconds; }
        @Override public void start(int totalSeconds) {}
        @Override public void pause() {}
        @Override public void resume() {}
        @Override public void stop() {}
        @Override public void setOnBreakDue(Runnable callback) {}
        @Override public ReadOnlyStringProperty countdownTextProperty() { return new SimpleStringProperty("20:00"); }
        @Override public ReadOnlyDoubleProperty progressProperty() { return new SimpleDoubleProperty(1.0); }
        @Override public ReadOnlyObjectProperty<TimerState> timerStateProperty() { return new SimpleObjectProperty<>(TimerState.IDLE); }
        @Override public TimerState getTimerState() { return TimerState.IDLE; }
        @Override public int getRemainingSeconds() { return 0; }
    }

    private static class StubOverlayService implements OverlayService {
        private boolean shown = false;
        @Override public void showOverlay() { shown = true; }
        @Override public void hideOverlay() { shown = false; }
        public boolean isShown() { return shown; }
    }

    private static class StubConfigurationService implements ConfigurationService {
        private final Settings settings = new Settings(10, 10, 3, true, "09:00", "19:00", false, true, true, true, false);
        @Override public Settings loadSettings() { return settings; }
        @Override public Settings getDefaultSettings() { return settings; }
        @Override public void saveSettings(Settings s) {}
        @Override public void validateSettings(Settings s) {}
    }
}
