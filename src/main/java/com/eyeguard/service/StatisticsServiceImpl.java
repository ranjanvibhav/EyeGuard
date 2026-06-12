package com.eyeguard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.eyeguard.model.Statistics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of StatisticsService using JSON serialization for local disk persistence.
 */
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Instant startTime = Instant.now();
    private final ScheduledExecutorService scheduler;

    private final IntegerProperty breaksTaken = new SimpleIntegerProperty(0);
    private final IntegerProperty snoozedCount = new SimpleIntegerProperty(0);
    private final StringProperty compliancePercent = new SimpleStringProperty("100%");
    private final IntegerProperty streakDays = new SimpleIntegerProperty(0);
    private final StringProperty sessionDuration = new SimpleStringProperty("Active for 0m");

    private String currentRecordedDate = LocalDate.now().toString();

    /**
     * Constructs a StatisticsServiceImpl targeting the specified filepath.
     *
     * @param filePath filepath on disk to serialize statistics
     */
    public StatisticsServiceImpl(final Path filePath) {
        this.filePath = filePath;
        loadStats();
        updateCompliance();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "eyeguard-stats-updater");
            t.setDaemon(true);
            return t;
        });
        this.scheduler.scheduleAtFixedRate(this::updateSessionDuration, 1, 1, TimeUnit.MINUTES);
        updateSessionDuration();
    }

    /**
     * Factory method creating a default instance targeting ~/.eyeguard/statistics.json.
     *
     * @return a default StatisticsServiceImpl instance
     */
    public static StatisticsServiceImpl createDefault() {
        return new StatisticsServiceImpl(Path.of(System.getProperty("user.home"),
                ".eyeguard", "statistics.json"));
    }

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

    private void loadStats() {
        if (!Files.exists(filePath)) {
            saveStats();
            return;
        }
        try {
            final Statistics stats = mapper.readValue(filePath.toFile(), Statistics.class);
            applyLoadedStats(stats);
        } catch (final IOException e) {
            LOGGER.error("Failed to load statistics", e);
        }
    }

    private void applyLoadedStats(final Statistics stats) {
        final String today = LocalDate.now().toString();
        final String yesterday = LocalDate.now().minusDays(1).toString();
        final String lastActive = stats.getLastActiveDate();

        if (today.equals(lastActive)) {
            breaksTaken.set(stats.getBreaksTaken());
            snoozedCount.set(stats.getSnoozedCount());
            streakDays.set(stats.getStreakDays());
        } else if (yesterday.equals(lastActive)) {
            streakDays.set(stats.getStreakDays());
        } else {
            streakDays.set(0);
        }
    }

    private void saveStats() {
        final Statistics stats = new Statistics();
        stats.setBreaksTaken(breaksTaken.get());
        stats.setSnoozedCount(snoozedCount.get());
        stats.setStreakDays(streakDays.get());
        stats.setLastActiveDate(LocalDate.now().toString());
        try {
            Files.createDirectories(filePath.getParent());
            mapper.writeValue(filePath.toFile(), stats);
        } catch (final IOException e) {
            LOGGER.error("Failed to save statistics", e);
        }
    }

    private void checkDateTransition() {
        final String today = LocalDate.now().toString();
        if (!today.equals(currentRecordedDate)) {
            breaksTaken.set(0);
            snoozedCount.set(0);
            currentRecordedDate = today;
        }
    }

    @Override
    public void recordBreakCompleted() {
        checkDateTransition();
        breaksTaken.set(breaksTaken.get() + 1);
        if (breaksTaken.get() == 1) {
            streakDays.set(streakDays.get() + 1);
        }
        updateCompliance();
        saveStats();
    }

    @Override
    public void recordBreakSnoozed() {
        checkDateTransition();
        snoozedCount.set(snoozedCount.get() + 1);
        updateCompliance();
        saveStats();
    }

    private void updateCompliance() {
        final int taken = breaksTaken.get();
        final int snoozed = snoozedCount.get();
        final int total = taken + snoozed;
        if (total == 0) {
            compliancePercent.set("100%");
            return;
        }
        final int percent = (taken * 100) / total;
        compliancePercent.set(percent + "%");
    }

    @Override
    public void updateSessionDuration() {
        final Duration dur = Duration.between(startTime, Instant.now());
        final long hours = dur.toHours();
        final long minutes = dur.toMinutesPart();
        final String text = hours > 0
                ? String.format("Active for %dh %dm", hours, minutes)
                : String.format("Active for %dm", minutes);
        runOnFxThread(() -> sessionDuration.set(text));
    }

    private void runOnFxThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    @Override
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
