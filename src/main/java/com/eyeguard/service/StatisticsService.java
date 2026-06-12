package com.eyeguard.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * Service interface for tracking daily break and compliance statistics.
 */
public interface StatisticsService {

    /**
     * Gets the breaks taken count property.
     *
     * @return breaks taken count property
     */
    IntegerProperty breaksTakenProperty();

    /**
     * Gets the breaks snoozed count property.
     *
     * @return breaks snoozed count property
     */
    IntegerProperty snoozedCountProperty();

    /**
     * Gets the compliance percentage property (e.g. "85%").
     *
     * @return compliance percentage string property
     */
    StringProperty compliancePercentProperty();

    /**
     * Gets the active day streak property.
     *
     * @return active day streak property
     */
    IntegerProperty streakDaysProperty();

    /**
     * Gets the current session active duration property (e.g. "Active for 1h 24m").
     *
     * @return session active duration property
     */
    StringProperty sessionDurationProperty();

    /**
     * Records a successfully completed break, updating daily stats and streaks.
     */
    void recordBreakCompleted();

    /**
     * Records a snoozed break, updating daily stats and compliance.
     */
    void recordBreakSnoozed();

    /**
     * Force updates the session duration property.
     */
    void updateSessionDuration();

    /**
     * Shuts down any background scheduling threads.
     */
    void shutdown();
}
