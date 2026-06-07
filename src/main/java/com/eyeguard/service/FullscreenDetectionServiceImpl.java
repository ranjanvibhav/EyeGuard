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
 * Implementation of FullscreenDetectionService polling system fullscreen status.
 */
public class FullscreenDetectionServiceImpl implements FullscreenDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FullscreenDetectionServiceImpl.class);
    private static final int POLL_INTERVAL_SECONDS = 15;

    private final SystemFullscreenProvider fullscreenProvider;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollFuture;
    private final BooleanProperty isFullscreenActive;
    private volatile boolean running;
    private Runnable onFullscreenEntered;
    private Runnable onFullscreenExited;

    /**
     * Constructs FullscreenDetectionServiceImpl with the given provider.
     *
     * @param provider OS-appropriate system fullscreen provider
     */
    public FullscreenDetectionServiceImpl(final SystemFullscreenProvider provider) {
        this.fullscreenProvider = provider;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "eyeguard-fullscreen-detector");
            t.setDaemon(true);
            return t;
        });
        this.isFullscreenActive = new SimpleBooleanProperty(false);
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
                0,
                POLL_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
        log.info("Fullscreen detection started");
    }

    void poll() {
        if (!running) {
            return;
        }
        final boolean fullscreen = fullscreenProvider.isFullscreenWindowPresent();
        runOnFxThread(() -> {
            final boolean wasFullscreen = isFullscreenActive.get();
            isFullscreenActive.set(fullscreen);
            if (!wasFullscreen && fullscreen) {
                log.info("Fullscreen app detected — pausing reminders");
                if (onFullscreenEntered != null) onFullscreenEntered.run();
            } else if (wasFullscreen && !fullscreen) {
                log.info("Fullscreen app exited — resuming reminders");
                if (onFullscreenExited != null) onFullscreenExited.run();
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
        log.info("Fullscreen detection stopped");
    }

    @Override
    public void setOnFullscreenEntered(final Runnable callback) {
        this.onFullscreenEntered = callback;
    }

    @Override
    public void setOnFullscreenExited(final Runnable callback) {
        this.onFullscreenExited = callback;
    }

    @Override
    public ReadOnlyBooleanProperty isFullscreenActiveProperty() {
        return isFullscreenActive;
    }

    @Override
    public boolean isFullscreenActive() {
        return isFullscreenActive.get();
    }

    private void runOnFxThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
