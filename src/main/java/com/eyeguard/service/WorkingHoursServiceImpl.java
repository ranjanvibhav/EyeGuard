package com.eyeguard.service;

import com.eyeguard.model.Settings;
import com.eyeguard.model.WorkingHours;
import java.time.LocalTime;
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
 * Implementation of WorkingHoursService polling the working hours configuration status.
 */
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private static final Logger log = LoggerFactory.getLogger(WorkingHoursServiceImpl.class);
    private static final int POLL_INTERVAL_SECONDS = 60;

    private final ConfigurationService configurationService;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollFuture;
    private final BooleanProperty withinWorkingHours;
    private volatile WorkingHours workingHours;
    private volatile boolean running;
    private Runnable onWorkingHoursStarted;
    private Runnable onWorkingHoursEnded;

    /**
     * Constructs WorkingHoursServiceImpl with the given configuration service.
     *
     * @param configurationService service to load and read settings files
     */
    public WorkingHoursServiceImpl(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "eyeguard-working-hours");
            t.setDaemon(true);
            return t;
        });
        this.withinWorkingHours = new SimpleBooleanProperty(true);
        this.running = false;
        WorkingHours initial = null;
        try {
            initial = WorkingHours.fromSettings(configurationService.loadSettings());
        } catch (final Exception e) {
            log.warn("Failed to load initial settings, using defaults", e);
            initial = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(19, 0), true, false);
        }
        this.workingHours = initial;
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
        log.info("Working hours service started");
    }

    void poll() {
        if (!running) {
            return;
        }
        final boolean currentlyWithin = workingHours.isWithinWorkingHours();
        runOnFxThread(() -> {
            final boolean wasWithin = withinWorkingHours.get();
            withinWorkingHours.set(currentlyWithin);
            if (!wasWithin && currentlyWithin) {
                log.info("Working hours started — resuming reminders");
                if (onWorkingHoursStarted != null) onWorkingHoursStarted.run();
            } else if (wasWithin && !currentlyWithin) {
                log.info("Working hours ended — pausing reminders");
                if (onWorkingHoursEnded != null) onWorkingHoursEnded.run();
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
        log.info("Working hours service stopped");
    }

    @Override
    public void reloadSettings() {
        try {
            final Settings settings = configurationService.loadSettings();
            workingHours = WorkingHours.fromSettings(settings);
            log.info("Working hours reloaded: {}", workingHours);
            poll();
        } catch (final Exception e) {
            log.error("Failed to reload working hours from settings", e);
        }
    }

    @Override
    public boolean isWithinWorkingHours() {
        return workingHours.isWithinWorkingHours();
    }

    @Override
    public ReadOnlyBooleanProperty withinWorkingHoursProperty() {
        return withinWorkingHours;
    }

    @Override
    public void setOnWorkingHoursStarted(final Runnable callback) {
        this.onWorkingHoursStarted = callback;
    }

    @Override
    public void setOnWorkingHoursEnded(final Runnable callback) {
        this.onWorkingHoursEnded = callback;
    }

    private void runOnFxThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
