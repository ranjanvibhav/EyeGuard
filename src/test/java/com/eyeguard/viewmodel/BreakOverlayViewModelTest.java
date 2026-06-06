package com.eyeguard.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link BreakOverlayViewModel} verifying state and transitions.
 */
class BreakOverlayViewModelTest {

    private static final String EXPECTED_COUNTDOWN = "20";
    private static final double EXPECTED_PROGRESS = 1.0;
    private static final String EXPECTED_ERROR = "";

    private static final String NEW_COUNTDOWN = "15";
    private static final double NEW_PROGRESS = 0.5;

    private BreakOverlayViewModel viewModel;

    /**
     * Sets up a fresh BreakOverlayViewModel instance before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new BreakOverlayViewModel();
    }

    /**
     * Verifies that the initial property values are correctly set.
     */
    @Test
    void testInitialPropertyValues() {
        assertEquals(EXPECTED_COUNTDOWN, viewModel.getCountdownText());
        assertEquals(EXPECTED_COUNTDOWN, viewModel.countdownTextProperty().get());

        assertEquals(EXPECTED_PROGRESS, viewModel.getRingProgress());
        assertEquals(EXPECTED_PROGRESS, viewModel.ringProgressProperty().get());

        assertFalse(viewModel.isDoneButtonEnabled());
        assertFalse(viewModel.doneButtonEnabledProperty().get());

        assertEquals(EXPECTED_ERROR, viewModel.getErrorMessage());
        assertEquals(EXPECTED_ERROR, viewModel.errorMessageProperty().get());
    }

    /**
     * Verifies that updating the countdown text property works as expected.
     */
    @Test
    void testSetCountdownText() {
        viewModel.countdownTextProperty().set(NEW_COUNTDOWN);
        assertEquals(NEW_COUNTDOWN, viewModel.getCountdownText());
    }

    /**
     * Verifies that updating the ring progress property works as expected.
     */
    @Test
    void testSetRingProgress() {
        viewModel.ringProgressProperty().set(NEW_PROGRESS);
        assertEquals(NEW_PROGRESS, viewModel.getRingProgress());
    }

    /**
     * Verifies that updating the done button enabled state property works as expected.
     */
    @Test
    void testSetDoneButtonEnabled() {
        viewModel.doneButtonEnabledProperty().set(true);
        assertTrue(viewModel.isDoneButtonEnabled());
    }
}
