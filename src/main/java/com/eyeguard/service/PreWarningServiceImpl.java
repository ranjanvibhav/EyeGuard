package com.eyeguard.service;

import com.eyeguard.model.TimerState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of PreWarningService coordination.
 */
public class PreWarningServiceImpl implements PreWarningService {

    private static final Logger log = LoggerFactory.getLogger(PreWarningServiceImpl.class);
    private static final int DEFAULT_WARNING_THRESHOLD = 120;
    private static final int TOAST_DURATION_SECONDS = 8;

    private final ToastService toastService;
    private final TrayService trayService;
    private volatile int warningThresholdSeconds;
    private volatile boolean warningFired;
    private ChangeListener<Number> progressListener;
    private ChangeListener<TimerState> stateListener;
    private TimerService attachedTimerService;

    /**
     * Constructs PreWarningServiceImpl.
     *
     * @param toastService service displaying notifications
     * @param trayService  service managing tray icon image states
     */
    public PreWarningServiceImpl(final ToastService toastService, final TrayService trayService) {
        this.toastService = toastService;
        this.trayService = trayService;
        this.warningThresholdSeconds = DEFAULT_WARNING_THRESHOLD;
        this.warningFired = false;
    }

    @Override
    public void attach(final TimerService timerService) {
        this.attachedTimerService = timerService;
        this.progressListener = (obs, old, newVal) -> handleProgressUpdate(timerService);
        this.stateListener = (obs, old, newVal) -> handleStateUpdate(newVal);
        timerService.progressProperty().addListener(progressListener);
        timerService.timerStateProperty().addListener(stateListener);
        log.info("Pre-warning service attached");
    }

    private void handleProgressUpdate(final TimerService timerService) {
        final int remaining = timerService.getRemainingSeconds();
        if (!warningFired
                && remaining <= warningThresholdSeconds
                && remaining > 0
                && timerService.getTimerState() == TimerState.RUNNING) {
            warningFired = true;
            Platform.runLater(() -> triggerPreWarning(remaining));
        }
        if (remaining <= 0 || timerService.getTimerState() != TimerState.RUNNING) {
            warningFired = false;
        }
    }

    private void handleStateUpdate(final TimerState newState) {
        if (newState == TimerState.RUNNING && warningFired) {
            warningFired = false;
            trayService.setWarningMode(false);
        }
        if (newState == TimerState.BREAK_DUE || newState == TimerState.STOPPED) {
            trayService.setWarningMode(false);
        }
    }

    @Override
    public void detach() {
        if (attachedTimerService != null) {
            if (progressListener != null) {
                attachedTimerService.progressProperty().removeListener(progressListener);
            }
            if (stateListener != null) {
                attachedTimerService.timerStateProperty().removeListener(stateListener);
            }
        }
        attachedTimerService = null;
        progressListener = null;
        stateListener = null;
        log.info("Pre-warning service detached");
    }

    private void triggerPreWarning(final int remainingSeconds) {
        final int minutes = remainingSeconds / 60;
        final int seconds = remainingSeconds % 60;
        final String message = minutes > 0
                ? "Eye break in " + minutes + " min — finish your thought"
                : "Eye break in " + seconds + "s — finish your thought";
        toastService.showToast(message, TOAST_DURATION_SECONDS);
        trayService.setWarningMode(true);
        log.info("Pre-warning triggered with {}s remaining", remainingSeconds);
    }

    @Override
    public void setWarningThresholdSeconds(final int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Warning threshold must be positive: " + seconds);
        }
        this.warningThresholdSeconds = seconds;
    }
}
