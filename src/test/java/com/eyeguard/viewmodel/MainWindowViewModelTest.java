package com.eyeguard.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link MainWindowViewModel} to verify the initial state.
 */
class MainWindowViewModelTest {

    private static final String EXPECTED_COUNTDOWN = "19:47";
    private static final double EXPECTED_PROGRESS = 0.85;
    private static final String EXPECTED_STATUS_TEXT = "ACTIVE";
    private static final String EXPECTED_STATUS_STYLE = "status-badge-active";
    private static final int EXPECTED_BREAKS_TAKEN = 8;
    private static final int EXPECTED_SNOOZED_COUNT = 2;
    private static final String EXPECTED_COMPLIANCE = "100%";
    private static final String EXPECTED_SESSION_DURATION = "Active for 2h 34m";
    private static final String EXPECTED_STREAK = "Streak: 5 days";
    private static final String EXPECTED_ERROR = "";

    private MainWindowViewModel viewModel;

    /**
     * Sets up a fresh instance of MainWindowViewModel before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new MainWindowViewModel();
    }

    /**
     * Verifies the initial values of all properties in MainWindowViewModel.
     */
    @Test
    void testInitialPropertyValues() {
        assertEquals(EXPECTED_COUNTDOWN, viewModel.getNextBreakCountdown());
        assertEquals(EXPECTED_COUNTDOWN, viewModel.nextBreakCountdownProperty().get());

        assertEquals(EXPECTED_PROGRESS, viewModel.getTimerProgress());
        assertEquals(EXPECTED_PROGRESS, viewModel.timerProgressProperty().get());

        assertEquals(EXPECTED_STATUS_TEXT, viewModel.getStatusText());
        assertEquals(EXPECTED_STATUS_TEXT, viewModel.statusTextProperty().get());

        assertEquals(EXPECTED_STATUS_STYLE, viewModel.getStatusStyle());
        assertEquals(EXPECTED_STATUS_STYLE, viewModel.statusStyleProperty().get());

        assertEquals(EXPECTED_BREAKS_TAKEN, viewModel.getBreaksTaken());
        assertEquals(EXPECTED_BREAKS_TAKEN, viewModel.breaksTakenProperty().get());

        assertEquals(EXPECTED_SNOOZED_COUNT, viewModel.getSnoozedCount());
        assertEquals(EXPECTED_SNOOZED_COUNT, viewModel.snoozedCountProperty().get());

        assertEquals(EXPECTED_COMPLIANCE, viewModel.getCompliancePercent());
        assertEquals(EXPECTED_COMPLIANCE, viewModel.compliancePercentProperty().get());

        assertEquals(EXPECTED_SESSION_DURATION, viewModel.getSessionDuration());
        assertEquals(EXPECTED_SESSION_DURATION, viewModel.sessionDurationProperty().get());

        assertEquals(EXPECTED_STREAK, viewModel.getStreakText());
        assertEquals(EXPECTED_STREAK, viewModel.streakTextProperty().get());

        assertEquals(EXPECTED_ERROR, viewModel.getErrorMessage());
        assertEquals(EXPECTED_ERROR, viewModel.errorMessageProperty().get());
    }
}
