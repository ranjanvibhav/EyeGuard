package com.eyeguard.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of IdleDetectionService managing background polling for user idle status.
 */
public class IdleDetectionServiceImpl implements IdleDetectionService {

    private static final Logger log = LoggerFactory.getLogger(IdleDetectionServiceImpl.class);
    private static final int DEFAULT_IDLE_THRESHOLD_SECONDS = 300;
    private static final int POLL_INTERVAL_SECONDS = 30;

    private final SystemIdleProvider idleProvider;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollFuture;
    private final BooleanProperty isIdle;
    private volatile int idleThresholdSeconds;
    private volatile boolean running;
    private Runnable onIdleDetected;
    private Runnable onActivityResumed;

    /**
     * Constructs the IdleDetectionServiceImpl with OS-appropriate system idle provider.
     *
     * @param idleProvider OS-specific system idle query provider
     */
    public IdleDetectionServiceImpl(final SystemIdleProvider idleProvider) {
        this.idleProvider = idleProvider;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "eyeguard-idle-detector");
            t.setDaemon(true);
            return t;
        });
        this.isIdle = new SimpleBooleanProperty(false);
        this.idleThresholdSeconds = DEFAULT_IDLE_THRESHOLD_SECONDS;
        this.running = false;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        running = true;
        pollFuture = scheduler.scheduleAtFixedRate(
                this::poll,
                POLL_INTERVAL_SECONDS,
                POLL_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
        log.info("Idle detection started with threshold: {}s", idleThresholdSeconds);
    }

    void poll() {
        if (!running) {
            return;
        }
        final long currentIdleSeconds = idleProvider.getIdleTimeSeconds();
        final boolean currentlyIdle = currentIdleSeconds >= idleThresholdSeconds;
        runOnFxThread(() -> {
            final boolean wasIdle = isIdle.get();
            isIdle.set(currentlyIdle);
            if (!wasIdle && currentlyIdle) {
                log.info("System idle detected after {}s", currentIdleSeconds);
                if (onIdleDetected != null) onIdleDetected.run();
            } else if (wasIdle && !currentlyIdle) {
                log.info("Activity resumed after idle");
                if (onActivityResumed != null) onActivityResumed.run();
            }
        });
    }

    @Override
    public void stop() {
        running = false;
        if (pollFuture != null) {
            pollFuture.cancel(false);
            pollFuture = null;
        }
        scheduler.shutdownNow();
        log.info("Idle detection stopped");
    }

    @Override
    public void setIdleThresholdSeconds(final int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Threshold seconds must be positive: " + seconds);
        }
        this.idleThresholdSeconds = seconds;
    }

    @Override
    public void setOnIdleDetected(final Runnable callback) {
        this.onIdleDetected = callback;
    }

    @Override
    public void setOnActivityResumed(final Runnable callback) {
        this.onActivityResumed = callback;
    }

    @Override
    public ReadOnlyBooleanProperty isIdleProperty() {
        return isIdle;
    }

    @Override
    public boolean isIdle() {
        return isIdle.get();
    }

    @Override
    public long getIdleSeconds() {
        return idleProvider.getIdleTimeSeconds();
    }

    private void runOnFxThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
