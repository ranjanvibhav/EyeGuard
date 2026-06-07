package com.eyeguard.model;

/**
 * Enum defining the possible states of the Do Not Disturb (DND) service.
 */
public enum DndState {
    /**
     * No DND is active; reminders are running normally.
     */
    INACTIVE,

    /**
     * Break is currently snoozed for a short duration.
     */
    SNOOZED,

    /**
     * Reminders are indefinitely paused by the user.
     */
    PAUSED,

    /**
     * Reminders are paused for a fixed meeting duration.
     */
    MEETING_MODE
}
