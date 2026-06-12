package com.eyeguard.service;

import com.eyeguard.model.Statistics;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceImplTest {

    private Path tempFile;
    private StatisticsServiceImpl service;
    private ObjectMapper mapper;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("eyeguard-stats-test", ".json");
        mapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (service != null) {
            service.shutdown();
        }
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testInitialAndIncrements() {
        service = new StatisticsServiceImpl(tempFile);
        assertEquals(0, service.breaksTakenProperty().get());
        assertEquals(0, service.snoozedCountProperty().get());
        assertEquals("100%", service.compliancePercentProperty().get());

        service.recordBreakCompleted();
        assertEquals(1, service.breaksTakenProperty().get());
        assertEquals(1, service.streakDaysProperty().get());
        assertEquals("100%", service.compliancePercentProperty().get());

        service.recordBreakSnoozed();
        assertEquals(1, service.snoozedCountProperty().get());
        assertEquals("50%", service.compliancePercentProperty().get());
    }

    @Test
    void testLoadYesterdayPreservesStreak() throws IOException {
        final Statistics initial = new Statistics();
        initial.setBreaksTaken(5);
        initial.setSnoozedCount(1);
        initial.setStreakDays(3);
        initial.setLastActiveDate(LocalDate.now().minusDays(1).toString());
        mapper.writeValue(tempFile.toFile(), initial);

        service = new StatisticsServiceImpl(tempFile);
        assertEquals(0, service.breaksTakenProperty().get());
        assertEquals(0, service.snoozedCountProperty().get());
        assertEquals(3, service.streakDaysProperty().get());
    }

    @Test
    void testLoadOlderResetsStreak() throws IOException {
        final Statistics initial = new Statistics();
        initial.setBreaksTaken(5);
        initial.setSnoozedCount(1);
        initial.setStreakDays(3);
        initial.setLastActiveDate(LocalDate.now().minusDays(2).toString());
        mapper.writeValue(tempFile.toFile(), initial);

        service = new StatisticsServiceImpl(tempFile);
        assertEquals(0, service.breaksTakenProperty().get());
        assertEquals(0, service.snoozedCountProperty().get());
        assertEquals(0, service.streakDaysProperty().get());
    }
}
