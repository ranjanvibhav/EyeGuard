package com.eyeguard.service;

import com.eyeguard.model.DndState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 * Service managing Do Not Disturb (DND) states including snoozes, pauses, and meeting modes.
 */
public interface DndService {

    /**
     * Snoozes reminders for the given minutes. Resets timer to minutes * 60.
     *
     * @param minutes the duration to snooze in minutes
     * @throws IllegalArgumentException if minutes <= 0
     */
    void snooze(int minutes);

    /**
     * Pauses reminders indefinitely. Stops the timer.
     *
     * @throws IllegalStateException if already paused or in meeting mode
     */
    void pause();

    /**
     * Resumes reminders from any DND state. Restarts timer from full reminder interval.
     *
     * @throws IllegalStateException if state is INACTIVE
     */
    void resume();

    /**
     * Pauses reminders for exactly the given minutes. Automatically resumes after.
     *
     * @param minutes the meeting duration in minutes
     * @throws IllegalArgumentException if minutes <= 0
     * @throws IllegalStateException if already in meeting mode or paused
     */
    void enableMeetingMode(int minutes);

    /**
     * Gets the observable DND state.
     *
     * @return the observable DND state property
     */
    ReadOnlyObjectProperty<DndState> dndStateProperty();

    /**
     * Gets the observable human-readable status text.
     *
     * @return the status text property
     */
    ReadOnlyStringProperty dndStatusTextProperty();

    /**
     * Gets the observable countdown text until auto-resume.
     *
     * @return the resume time countdown property
     */
    ReadOnlyStringProperty resumeTimeTextProperty();

    /**
     * Gets the current DndState synchronously.
     *
     * @return the current DndState
     */
    DndState getDndState();
}
