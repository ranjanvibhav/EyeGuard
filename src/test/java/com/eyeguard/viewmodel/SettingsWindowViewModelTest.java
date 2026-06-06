package com.eyeguard.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link SettingsWindowViewModel} checking state initialization and mutation.
 */
class SettingsWindowViewModelTest {

    private static final int EXPECTED_INTERVAL = 20;
    private static final int EXPECTED_BREAK = 20;
    private static final String EXPECTED_SNOOZE = "5 minutes";
    private static final String EXPECTED_START = "9:00 AM";
    private static final String EXPECTED_END = "7:00 PM";
    private static final String EXPECTED_ERROR = "";

    private static final int NEW_INTERVAL = 30;
    private static final String NEW_ERROR = "Invalid configuration value";

    private SettingsWindowViewModel viewModel;

    /**
     * Sets up a fresh SettingsWindowViewModel instance before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new SettingsWindowViewModel();
    }

    /**
     * Verifies that the initial property values are correctly set.
     */
    @Test
    void testInitialPropertyValues() {
        assertEquals(EXPECTED_INTERVAL, viewModel.getReminderIntervalMinutes());
        assertEquals(EXPECTED_INTERVAL, viewModel.reminderIntervalMinutesProperty().get());

        assertEquals(EXPECTED_BREAK, viewModel.getBreakDurationSeconds());
        assertEquals(EXPECTED_BREAK, viewModel.breakDurationSecondsProperty().get());

        assertEquals(EXPECTED_SNOOZE, viewModel.getSnoozeDuration());
        assertEquals(EXPECTED_SNOOZE, viewModel.snoozeDurationProperty().get());

        assertTrue(viewModel.isWorkingHoursEnabled());
        assertTrue(viewModel.workingHoursEnabledProperty().get());

        assertEquals(EXPECTED_START, viewModel.getWorkStartTime());
        assertEquals(EXPECTED_START, viewModel.workStartTimeProperty().get());

        assertEquals(EXPECTED_END, viewModel.getWorkEndTime());
        assertEquals(EXPECTED_END, viewModel.workEndTimeProperty().get());

        assertFalse(viewModel.isWeekendRemindersEnabled());
        assertFalse(viewModel.weekendRemindersEnabledProperty().get());

        assertTrue(viewModel.isFullscreenPauseEnabled());
        assertTrue(viewModel.fullscreenPauseEnabledProperty().get());

        assertTrue(viewModel.isIdleDetectionEnabled());
        assertTrue(viewModel.idleDetectionEnabledProperty().get());

        assertTrue(viewModel.isSoundAlertsEnabled());
        assertTrue(viewModel.soundAlertsEnabledProperty().get());

        assertFalse(viewModel.isLaunchAtStartupEnabled());
        assertFalse(viewModel.launchAtStartupEnabledProperty().get());

        assertEquals(EXPECTED_ERROR, viewModel.getErrorMessage());
        assertEquals(EXPECTED_ERROR, viewModel.errorMessageProperty().get());
    }

    /**
     * Verifies that updating the reminder interval works as expected.
     */
    @Test
    void testSetReminderIntervalMinutes() {
        viewModel.reminderIntervalMinutesProperty().set(NEW_INTERVAL);
        assertEquals(NEW_INTERVAL, viewModel.getReminderIntervalMinutes());
    }

    /**
     * Verifies that updating the working hours enabled state works as expected.
     */
    @Test
    void testSetWorkingHoursEnabled() {
        viewModel.workingHoursEnabledProperty().set(false);
        assertFalse(viewModel.isWorkingHoursEnabled());
    }

    /**
     * Verifies that updating the error message works as expected.
     */
    @Test
    void testSetErrorMessage() {
        viewModel.errorMessageProperty().set(NEW_ERROR);
        assertEquals(NEW_ERROR, viewModel.getErrorMessage());
    }
}
