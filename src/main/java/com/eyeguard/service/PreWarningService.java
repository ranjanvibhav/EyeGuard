package com.eyeguard.service;

/**
 * Service to manage the pre-break warnings and status tray integrations.
 */
public interface PreWarningService {

    /**
     * Listens to the given timer service to trigger warning states at warning thresholds.
     *
     * @param timerService countdown timer service to listen to
     */
    void attach(TimerService timerService);

    /**
     * Detaches from the active timer service and clears listeners.
     */
    void detach();

    /**
     * Sets the warning threshold duration in seconds.
     *
     * @param seconds warning threshold seconds before break
     */
    void setWarningThresholdSeconds(int seconds);
}
