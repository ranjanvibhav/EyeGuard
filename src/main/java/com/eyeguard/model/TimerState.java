package com.eyeguard.model;

/**
 * Lifecycle states of the core countdown timer in EyeGuard.
 */
public enum TimerState {
    
    /**
     * The timer has not been started yet.
     */
    IDLE,

    /**
     * The timer is actively counting down.
     */
    RUNNING,

    /**
     * The timer is paused; countdown is frozen.
     */
    PAUSED,

    /**
     * The countdown has reached zero; a break is due.
     */
    BREAK_DUE,

    /**
     * The timer has been explicitly stopped.
     */
    STOPPED
}
