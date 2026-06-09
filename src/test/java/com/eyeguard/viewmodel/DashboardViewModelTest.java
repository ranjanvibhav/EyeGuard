package com.eyeguard.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link DashboardViewModel} verifying its state properties and mutations.
 */
class DashboardViewModelTest {

    private DashboardViewModel viewModel;

    /**
     * Sets up a fresh DashboardViewModel instance before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new DashboardViewModel();
    }

    /**
     * Verifies that the initial property values are correctly configured.
     */
    @Test
    void testInitialValues() {
        assertEquals("19:47", viewModel.getNextBreakCountdown());
        assertEquals("19:47", viewModel.nextBreakCountdownProperty().get());
        assertEquals(0.85, viewModel.getTimerProgress());
        assertEquals(0.85, viewModel.timerProgressProperty().get());
        assertEquals("Active", viewModel.getActiveStatusText());
        assertEquals("Active", viewModel.activeStatusTextProperty().get());
        assertEquals("status-text-active", viewModel.getActiveStatusStyle());
        assertEquals("status-text-active", viewModel.activeStatusStyleProperty().get());
        assertEquals(8, viewModel.getBreaksTaken());
        assertEquals(8, viewModel.breaksTakenProperty().get());
        assertEquals(2, viewModel.getSnoozedCount());
        assertEquals(2, viewModel.snoozedCountProperty().get());
        assertEquals("80%", viewModel.getCompliancePercent());
        assertEquals("80%", viewModel.compliancePercentProperty().get());
        assertEquals(5, viewModel.getStreakDays());
        assertEquals(5, viewModel.streakDaysProperty().get());
        assertEquals("Active for 2h 34m", viewModel.getSessionDuration());
        assertEquals("Active for 2h 34m", viewModel.sessionDurationProperty().get());
        assertEquals("● Tracking", viewModel.getIdleStatusText());
        assertEquals("● Tracking", viewModel.idleStatusTextProperty().get());
        assertEquals("status-text-active", viewModel.getIdleStatusStyle());
        assertEquals("status-text-active", viewModel.idleStatusStyleProperty().get());
        assertEquals("", viewModel.getErrorMessage());
        assertEquals("", viewModel.errorMessageProperty().get());
    }

    /**
     * Verifies that setting nextBreakCountdown updates correctly.
     */
    @Test
    void testSetNextBreakCountdown() {
        viewModel.nextBreakCountdownProperty().set("15:00");
        assertEquals("15:00", viewModel.getNextBreakCountdown());
    }

    /**
     * Verifies that setting timerProgress updates correctly.
     */
    @Test
    void testSetTimerProgress() {
        viewModel.timerProgressProperty().set(0.5);
        assertEquals(0.5, viewModel.getTimerProgress());
    }

    /**
     * Verifies that setting breaksTaken updates correctly.
     */
    @Test
    void testSetBreaksTaken() {
        viewModel.breaksTakenProperty().set(10);
        assertEquals(10, viewModel.getBreaksTaken());
    }

    /**
     * Verifies that setting streakDays updates correctly.
     */
    @Test
    void testSetStreakDays() {
        viewModel.streakDaysProperty().set(0);
        assertEquals(0, viewModel.getStreakDays());
    }

    /**
     * Verifies that setting compliancePercent updates correctly.
     */
    @Test
    void testSetCompliancePercent() {
        viewModel.compliancePercentProperty().set("95%");
        assertEquals("95%", viewModel.getCompliancePercent());
    }

    /**
     * Verifies that setting activeStatusStyle updates correctly.
     */
    @Test
    void testSetActiveStatusStyle() {
        viewModel.activeStatusStyleProperty().set("status-text-inactive");
        assertEquals("status-text-inactive", viewModel.getActiveStatusStyle());
    }

    @Test
    void testConstructorWithServices() {
        com.eyeguard.service.TimerService timer = org.mockito.Mockito.mock(com.eyeguard.service.TimerService.class);
        com.eyeguard.service.DndService dnd = org.mockito.Mockito.mock(com.eyeguard.service.DndService.class);
        com.eyeguard.service.IdleDetectionService idle = org.mockito.Mockito.mock(com.eyeguard.service.IdleDetectionService.class);
        org.mockito.Mockito.when(timer.countdownTextProperty()).thenReturn(new javafx.beans.property.SimpleStringProperty("12:34"));
        org.mockito.Mockito.when(timer.progressProperty()).thenReturn(new javafx.beans.property.SimpleDoubleProperty(0.6));
        org.mockito.Mockito.when(timer.timerStateProperty()).thenReturn(new javafx.beans.property.SimpleObjectProperty<>(com.eyeguard.model.TimerState.RUNNING));
        org.mockito.Mockito.when(timer.getTimerState()).thenReturn(com.eyeguard.model.TimerState.RUNNING);
        org.mockito.Mockito.when(dnd.dndStatusTextProperty()).thenReturn(new javafx.beans.property.SimpleStringProperty("Reminders active"));
        org.mockito.Mockito.when(dnd.getDndState()).thenReturn(com.eyeguard.model.DndState.INACTIVE);
        org.mockito.Mockito.when(idle.isIdleProperty()).thenReturn(new javafx.beans.property.SimpleBooleanProperty(false));
        DashboardViewModel model = new DashboardViewModel(timer, dnd, idle);
        assertEquals("12:34", model.getNextBreakCountdown());
        assertEquals(0.6, model.getTimerProgress());
        assertEquals("Active", model.getActiveStatusText());
        assertEquals("status-text-active", model.getActiveStatusStyle());
    }
}
