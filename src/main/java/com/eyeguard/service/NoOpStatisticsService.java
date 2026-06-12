package com.eyeguard.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Fallback memory-only StatisticsService implementation that performs no disk operations.
 */
public class NoOpStatisticsService implements StatisticsService {

    private final IntegerProperty breaksTaken = new SimpleIntegerProperty(0);
    private final IntegerProperty snoozedCount = new SimpleIntegerProperty(0);
    private final StringProperty compliancePercent = new SimpleStringProperty("100%");
    private final IntegerProperty streakDays = new SimpleIntegerProperty(0);
    private final StringProperty sessionDuration = new SimpleStringProperty("Active for 0m");

    @Override
    public IntegerProperty breaksTakenProperty() {
        return breaksTaken;
    }

    @Override
    public IntegerProperty snoozedCountProperty() {
        return snoozedCount;
    }

    @Override
    public StringProperty compliancePercentProperty() {
        return compliancePercent;
    }

    @Override
    public IntegerProperty streakDaysProperty() {
        return streakDays;
    }

    @Override
    public StringProperty sessionDurationProperty() {
        return sessionDuration;
    }

    @Override
    public void recordBreakCompleted() {
        // No-op
    }

    @Override
    public void recordBreakSnoozed() {
        // No-op
    }

    @Override
    public void updateSessionDuration() {
        // No-op
    }

    @Override
    public void shutdown() {
        // No-op
    }
}
