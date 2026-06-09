package com.eyeguard.service;

import com.eyeguard.model.BreakSession;
import com.eyeguard.model.Settings;
import com.eyeguard.viewmodel.BreakOverlayViewModel;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BreakService. Coordinates the full-screen overlay visibility,
 * manages the break countdown, and restarts/resets the main timer.
 */
public class BreakServiceImpl implements BreakService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BreakServiceImpl.class);

    private final TimerService timerService;
    private final OverlayService overlayService;
    private final BreakOverlayViewModel overlayViewModel;
    private final ConfigurationService configurationService;
    private final ScheduledExecutorService breakScheduler;
    private ScheduledFuture<?> breakFuture;
    private BreakSession currentSession;
    private final IntegerProperty breakCountdown;
    private final BooleanProperty breakActive;

    /**
     * Constructs a BreakServiceImpl with dependencies.
     *
     * @param timerService         the core timer service
     * @param overlayService       the overlay stage service
     * @param overlayViewModel     the break overlay view model
     * @param configurationService the application configuration service
     */
    public BreakServiceImpl(
            final TimerService timerService,
            final OverlayService overlayService,
            final BreakOverlayViewModel overlayViewModel,
            final ConfigurationService configurationService) {
        this.timerService = timerService;
        this.overlayService = overlayService;
        this.overlayViewModel = overlayViewModel;
        this.configurationService = configurationService;
        this.breakScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            final Thread thread = new Thread(runnable, "eyeguard-break-timer");
            thread.setDaemon(true);
            return thread;
        });
        this.breakCountdown = new SimpleIntegerProperty(20);
        this.breakActive = new SimpleBooleanProperty(false);

        this.overlayViewModel.setOnDone(this::completeBreak);
        this.overlayViewModel.setOnSnooze(this::snoozeBreak);
    }

    @Override
    public void startBreak() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Must be called on the JavaFX Application Thread");
        }
        if (breakActive.get()) {
            return;
        }
        final Settings settings = loadSettingsSafely();
        final int duration = settings.getBreakDurationSeconds();
        currentSession = new BreakSession(LocalDateTime.now(), duration);
        breakCountdown.set(duration);
        breakActive.set(true);
        initOverlayViewModel(duration);
        overlayService.showOverlay();
        cancelBreakFuture();
        breakFuture = breakScheduler.scheduleAtFixedRate(this::tickBreak, 1, 1, TimeUnit.SECONDS);
        LOGGER.info("Break started, duration: {}s", duration);
    }

    private void tickBreak() {
        if (!breakActive.get()) {
            return;
        }
        final int nextVal = breakCountdown.get() - 1;
        Platform.runLater(() -> updateBreakCountdown(nextVal));
    }

    private void updateBreakCountdown(final int nextVal) {
        if (!breakActive.get()) {
            return;
        }
        final int clampedVal = Math.max(0, nextVal);
        breakCountdown.set(clampedVal);
        overlayViewModel.countdownTextProperty().set(String.valueOf(clampedVal));
        final double progress = (double) clampedVal / currentSession.getDurationSeconds();
        overlayViewModel.ringProgressProperty().set(progress);
        if (clampedVal <= 0) {
            overlayViewModel.doneButtonEnabledProperty().set(true);
            cancelBreakFuture();
            LOGGER.info("Break countdown completed");
        }
    }

    @Override
    public void completeBreak() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Must be called on the JavaFX Application Thread");
        }
        if (currentSession != null && !currentSession.isFinished()) {
            currentSession.complete(LocalDateTime.now());
        }
        cancelBreakFuture();
        breakActive.set(false);
        overlayService.hideOverlay();
        final Settings settings = loadSettingsSafely();
        timerService.reset(settings.getReminderIntervalMinutes() * 60);
        LOGGER.info("Break completed successfully");
    }

    @Override
    public void snoozeBreak() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Must be called on the JavaFX Application Thread");
        }
        if (currentSession != null && !currentSession.isFinished()) {
            currentSession.snooze(LocalDateTime.now());
        }
        cancelBreakFuture();
        breakActive.set(false);
        overlayService.hideOverlay();
        final Settings settings = loadSettingsSafely();
        timerService.reset(settings.getSnoozeDurationMinutes() * 60);
        LOGGER.info("Break snoozed");
    }

    @Override
    public ReadOnlyIntegerProperty breakCountdownProperty() {
        return breakCountdown;
    }

    @Override
    public ReadOnlyBooleanProperty breakActiveProperty() {
        return breakActive;
    }

    /**
     * Shuts down the background scheduler execution.
     */
    public void shutdown() {
        cancelBreakFuture();
        breakScheduler.shutdownNow();
    }

    private Settings loadSettingsSafely() {
        try {
            return configurationService.loadSettings();
        } catch (final Exception e) {
            return configurationService.getDefaultSettings();
        }
    }

    private void initOverlayViewModel(final int duration) {
        overlayViewModel.countdownTextProperty().set(String.valueOf(duration));
        overlayViewModel.ringProgressProperty().set(1.0);
        overlayViewModel.doneButtonEnabledProperty().set(false);
        overlayViewModel.errorMessageProperty().set("");
    }

    private void cancelBreakFuture() {
        if (breakFuture != null) {
            breakFuture.cancel(false);
            breakFuture = null;
        }
    }
}
