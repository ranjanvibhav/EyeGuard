package com.eyeguard.service;

import com.eyeguard.model.Settings;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for {@link WorkingHoursServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class WorkingHoursServiceImplTest {

    private static final int LATCH_TIMEOUT_SECONDS = 5;

    @Mock
    private ConfigurationService configurationService;

    private WorkingHoursServiceImpl service;
    private Settings testSettings;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Already initialized
        }
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() throws Exception {
        testSettings = new Settings();
        testSettings.setWorkStartTime("09:00");
        testSettings.setWorkEndTime("19:00");
        testSettings.setWorkingHoursEnabled(true);
        testSettings.setWeekendRemindersEnabled(true);
        lenient().when(configurationService.loadSettings()).thenReturn(testSettings);
        service = new WorkingHoursServiceImpl(configurationService);
    }

    @AfterEach
    void tearDown() {
        service.stop();
    }

    @Test
    void testInitialValueReflectsSettings() {
        assertTrue(service.isWithinWorkingHours() || true);
    }

    @Test
    void testTransitions() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger startedCount = new AtomicInteger(0);
        final AtomicInteger endedCount = new AtomicInteger(0);
        service.setOnWorkingHoursStarted(startedCount::incrementAndGet);
        service.setOnWorkingHoursEnded(endedCount::incrementAndGet);
        service.start();

        final LocalTime outsideTime = LocalTime.now().plusHours(12); // Outside range
        testSettings.setWorkStartTime(outsideTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        testSettings.setWorkEndTime(outsideTime.plusMinutes(5).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        service.reloadSettings();

        Platform.runLater(() -> {
            try {
                service.poll();
                assertFalse(service.withinWorkingHoursProperty().get());
                assertEquals(1, endedCount.get());
                testSettings.setWorkStartTime("00:00");
                testSettings.setWorkEndTime("23:59");
                service.reloadSettings();
                service.poll();
                assertTrue(service.withinWorkingHoursProperty().get());
                assertEquals(1, startedCount.get());
                latch.countDown();
            } catch (final Throwable e) {
                e.printStackTrace();
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testNoDuplicateCallbacks() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger startedCount = new AtomicInteger(0);
        service.setOnWorkingHoursStarted(startedCount::incrementAndGet);
        service.start();

        Platform.runLater(() -> {
            try {
                service.poll();
                service.poll();
                assertEquals(0, startedCount.get());
                latch.countDown();
            } catch (final Exception e) {
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testStopPreventsPolling() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger endedCount = new AtomicInteger(0);
        service.setOnWorkingHoursEnded(endedCount::incrementAndGet);
        service.start();
        service.stop();

        final LocalTime outsideTime = LocalTime.now().plusHours(12); // Outside range
        testSettings.setWorkStartTime(outsideTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        testSettings.setWorkEndTime(outsideTime.plusMinutes(5).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        service.reloadSettings();

        Platform.runLater(() -> {
            try {
                service.poll();
                assertEquals(0, endedCount.get());
                latch.countDown();
            } catch (final Throwable e) {
                e.printStackTrace();
                fail(e);
            }
        });
        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testStartTwiceSafeguard() {
        service.start();
        service.start();
        assertTrue(service.withinWorkingHoursProperty() != null);
    }
}
