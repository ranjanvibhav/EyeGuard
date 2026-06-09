package com.eyeguard.service;

import com.eyeguard.model.DndState;
import com.eyeguard.model.SettingsConstraints;
import com.eyeguard.model.TimerState;
import com.eyeguard.util.TimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of DndService coordinating reminder pauses, snoozes, and meeting modes.
 */
public class DndServiceImpl implements DndService {

    private static final Logger log = LoggerFactory.getLogger(DndServiceImpl.class);

    private final TimerService timerService;
    private final ConfigurationService configurationService;
    private final ScheduledExecutorService dndScheduler;
    private ScheduledFuture<?> dndFuture;
    private ScheduledFuture<?> resumeCountdownFuture;
    private final ObjectProperty<DndState> dndState;
    private final StringProperty dndStatusText;
    private final StringProperty resumeTimeText;
    private volatile int dndRemainingSeconds;

    /**
     * Constructs DndServiceImpl with dependency services.
     *
     * @param timerService         the countdown timer service
     * @param configurationService the configuration persistence service
     */
    public DndServiceImpl(final TimerService timerService,
                          final ConfigurationService configurationService) {
        this.timerService = timerService;
        this.configurationService = configurationService;
        this.dndScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "eyeguard-dnd");
            t.setDaemon(true);
            return t;
        });
        this.dndState = new SimpleObjectProperty<>(DndState.INACTIVE);
        this.dndStatusText = new SimpleStringProperty("Reminders active");
        this.resumeTimeText = new SimpleStringProperty("");
    }

    @Override
    public void snooze(final int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be positive: " + minutes);
        }
        final int snoozeSeconds = minutes * 60;
        if (timerService.getRemainingSeconds() >= snoozeSeconds) {
            log.info("Snooze ignored: remaining seconds {} >= {}", timerService.getRemainingSeconds(), snoozeSeconds);
            return;
        }
        cancelResumeCountdown();
        dndRemainingSeconds = snoozeSeconds;
        timerService.reset(snoozeSeconds);
        runOnFxThread(() -> {
            dndState.set(DndState.SNOOZED);
            resumeTimeText.set(TimeFormatter.formatSeconds(snoozeSeconds));
            updateStatusText();
        });
        resumeCountdownFuture = dndScheduler.scheduleAtFixedRate(this::tickDndCountdown, 1, 1, TimeUnit.SECONDS);
        log.info("Snoozed for {} minutes", minutes);
    }

    private void tickDndCountdown() {
        final DndState current = dndState.get();
        if (current != DndState.SNOOZED && current != DndState.MEETING_MODE) {
            return;
        }
        dndRemainingSeconds--;
        final int remaining = dndRemainingSeconds;
        Platform.runLater(() -> updateCountdownUi(remaining));
    }

    private void updateCountdownUi(final int remaining) {
        resumeTimeText.set(TimeFormatter.formatSeconds(remaining));
        updateStatusText();
        if (remaining <= 0) {
            cancelResumeCountdown();
            autoResume();
        }
    }

    private void autoResume() {
        dndState.set(DndState.INACTIVE);
        resumeTimeText.set("");
        updateStatusText();
        final int totalSeconds = loadReminderInterval() * 60;
        timerService.reset(totalSeconds);
        log.info("DND auto-resumed — timer restarted");
    }

    @Override
    public void pause() {
        if (dndState.get() != DndState.INACTIVE) {
            throw new IllegalStateException("Cannot pause from state: " + dndState.get());
        }
        cancelResumeCountdown();
        if (timerService.getTimerState() == TimerState.RUNNING) {
            timerService.pause();
        }
        runOnFxThread(() -> {
            dndState.set(DndState.PAUSED);
            resumeTimeText.set("");
            updateStatusText();
        });
        log.info("Reminders paused indefinitely");
    }

    @Override
    public void resume() {
        if (dndState.get() == DndState.INACTIVE) {
            throw new IllegalStateException("Cannot resume when not in DND state");
        }
        cancelResumeCountdown();
        runOnFxThread(() -> {
            dndState.set(DndState.INACTIVE);
            resumeTimeText.set("");
            updateStatusText();
        });
        final int totalSeconds = loadReminderInterval() * 60;
        if (timerService.getTimerState() == TimerState.PAUSED) {
            timerService.resume();
        } else {
            timerService.reset(totalSeconds);
        }
        log.info("Reminders resumed");
    }

    @Override
    public void enableMeetingMode(final int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be positive: " + minutes);
        }
        if (dndState.get() != DndState.INACTIVE) {
            throw new IllegalStateException("Cannot start meeting mode from: " + dndState.get());
        }
        final int meetingSeconds = minutes * 60;
        dndRemainingSeconds = meetingSeconds;
        if (timerService.getTimerState() == TimerState.RUNNING) {
            timerService.pause();
        }
        runOnFxThread(() -> {
            dndState.set(DndState.MEETING_MODE);
            resumeTimeText.set(TimeFormatter.formatSeconds(meetingSeconds));
            updateStatusText();
        });
        resumeCountdownFuture = dndScheduler.scheduleAtFixedRate(
                this::tickDndCountdown, 1, 1, TimeUnit.SECONDS);
        log.info("Meeting mode enabled for {} minutes", minutes);
    }

    private void updateStatusText() {
        switch (dndState.get()) {
            case INACTIVE -> dndStatusText.set("Reminders active");
            case SNOOZED -> dndStatusText.set("Snoozed — resumes in " + resumeTimeText.get());
            case PAUSED -> dndStatusText.set("Paused — click Resume to continue");
            case MEETING_MODE -> dndStatusText.set("Meeting mode — resumes in " + resumeTimeText.get());
        }
    }

    private int loadReminderInterval() {
        try {
            return configurationService.loadSettings().getReminderIntervalMinutes();
        } catch (final Exception e) {
            return SettingsConstraints.DEFAULT_REMINDER_INTERVAL;
        }
    }

    private void cancelResumeCountdown() {
        if (resumeCountdownFuture != null) {
            resumeCountdownFuture.cancel(false);
            resumeCountdownFuture = null;
        }
    }

    /**
     * Cleans up scheduler resources and cancels countdowns.
     */
    public void shutdown() {
        cancelResumeCountdown();
        dndScheduler.shutdownNow();
    }

    @Override
    public ReadOnlyObjectProperty<DndState> dndStateProperty() {
        return dndState;
    }

    @Override
    public ReadOnlyStringProperty dndStatusTextProperty() {
        return dndStatusText;
    }

    @Override
    public ReadOnlyStringProperty resumeTimeTextProperty() {
        return resumeTimeText;
    }

    @Override
    public DndState getDndState() {
        return dndState.get();
    }

    private void runOnFxThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
