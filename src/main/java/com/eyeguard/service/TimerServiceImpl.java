package com.eyeguard.service;

import com.eyeguard.model.TimerState;
import com.eyeguard.util.TimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of TimerService managing background scheduled executor timer tick tasks
 * and updating observable properties on the JavaFX Application Thread.
 */
public class TimerServiceImpl implements TimerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerServiceImpl.class);

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;
    
    private final StringProperty countdownText;
    private final DoubleProperty progress;
    private final ObjectProperty<TimerState> timerState;

    private volatile int remainingSeconds;
    private volatile int totalSeconds;
    private Runnable onBreakDue;

    /**
     * Constructs a TimerServiceImpl with default settings and a daemon background thread scheduler.
     */
    public TimerServiceImpl() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            final Thread thread = new Thread(runnable, "eyeguard-timer");
            thread.setDaemon(true);
            return thread;
        });
        this.countdownText = new SimpleStringProperty("20:00");
        this.progress = new SimpleDoubleProperty(1.0);
        this.timerState = new SimpleObjectProperty<>(TimerState.IDLE);
    }

    @Override
    public void start(final int totalSeconds) {
        if (totalSeconds <= 0) {
            throw new IllegalArgumentException("Total seconds must be positive: " + totalSeconds);
        }
        if (scheduledFuture != null) {
            throw new IllegalStateException("Timer is already running.");
        }
        this.totalSeconds = totalSeconds;
        this.remainingSeconds = totalSeconds;

        runOnFxThread(() -> {
            countdownText.set(TimeFormatter.formatSeconds(totalSeconds));
            progress.set(1.0);
            timerState.set(TimerState.RUNNING);
        });

        scheduledFuture = scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
        LOGGER.info("Timer started: {}", TimeFormatter.formatSeconds(totalSeconds));
    }

    private void tick() {
        if (timerState.get() != TimerState.RUNNING) {
            return;
        }
        remainingSeconds--;
        final double newProgress = TimeFormatter.calculateProgress(remainingSeconds, totalSeconds);
        final String newText = TimeFormatter.formatSeconds(Math.max(0, remainingSeconds));

        runOnFxThread(() -> {
            countdownText.set(newText);
            progress.set(newProgress);
            if (remainingSeconds <= 0) {
                handleTimerCompletion();
            }
        });
    }

    private void handleTimerCompletion() {
        timerState.set(TimerState.BREAK_DUE);
        cancelScheduledFuture();
        LOGGER.info("Break due — timer completed");
        if (onBreakDue != null) {
            onBreakDue.run();
        }
    }

    @Override
    public void pause() {
        if (scheduledFuture == null) {
            throw new IllegalStateException("Timer is not running.");
        }
        cancelScheduledFuture();
        runOnFxThread(() -> timerState.set(TimerState.PAUSED));
        LOGGER.info("Timer paused at {}", TimeFormatter.formatSeconds(remainingSeconds));
    }

    @Override
    public void resume() {
        if (scheduledFuture != null) {
            throw new IllegalStateException("Timer is already running.");
        }
        if (timerState.get() != TimerState.PAUSED) {
            throw new IllegalStateException("Timer is not paused.");
        }
        runOnFxThread(() -> timerState.set(TimerState.RUNNING));
        scheduledFuture = scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
        LOGGER.info("Timer resumed with {} seconds remaining", remainingSeconds);
    }

    @Override
    public void reset(final int totalSeconds) {
        cancelScheduledFuture();
        runOnFxThread(() -> timerState.set(TimerState.IDLE));
        start(totalSeconds);
        LOGGER.info("Timer reset");
    }

    @Override
    public void stop() {
        cancelScheduledFuture();
        scheduler.shutdownNow();
        runOnFxThread(() -> timerState.set(TimerState.STOPPED));
        LOGGER.info("Timer stopped");
    }

    @Override
    public void setOnBreakDue(final Runnable callback) {
        this.onBreakDue = callback;
    }

    @Override
    public ReadOnlyStringProperty countdownTextProperty() {
        return countdownText;
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    @Override
    public ReadOnlyObjectProperty<TimerState> timerStateProperty() {
        return timerState;
    }

    @Override
    public TimerState getTimerState() {
        return timerState.get();
    }

    @Override
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    private void runOnFxThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    private void cancelScheduledFuture() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
    }
}
