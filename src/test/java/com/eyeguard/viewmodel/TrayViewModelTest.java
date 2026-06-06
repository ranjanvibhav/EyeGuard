package com.eyeguard.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TrayViewModel} verifying its state and properties.
 */
class TrayViewModelTest {

    private TrayViewModel viewModel;

    /**
     * Sets up a fresh TrayViewModel instance before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new TrayViewModel();
    }

    /**
     * Verifies that the initial property values are correctly set.
     */
    @Test
    void testInitialValues() {
        assertEquals("EyeGuard — Next break in: 19:47", viewModel.getTooltipText());
        assertEquals("EyeGuard — Next break in: 19:47", viewModel.tooltipTextProperty().get());
        assertEquals("Next break in: 19:47", viewModel.getStatusMenuItemText());
        assertEquals("Next break in: 19:47", viewModel.statusMenuItemTextProperty().get());
        assertFalse(viewModel.isPaused());
        assertFalse(viewModel.isPausedProperty().get());
        assertEquals("Pause Reminders", viewModel.getPauseMenuItemText());
        assertEquals("Pause Reminders", viewModel.pauseMenuItemTextProperty().get());
        assertEquals("", viewModel.getErrorMessage());
        assertEquals("", viewModel.errorMessageProperty().get());
    }

    /**
     * Verifies that togglePause correctly updates paused state and menu item label.
     */
    @Test
    void testTogglePause() {
        viewModel.togglePause();
        assertTrue(viewModel.isPaused());
        assertEquals("Resume Reminders", viewModel.getPauseMenuItemText());

        viewModel.togglePause();
        assertFalse(viewModel.isPaused());
        assertEquals("Pause Reminders", viewModel.getPauseMenuItemText());
    }

    /**
     * Verifies setting and getting the error message property.
     */
    @Test
    void testSetErrorMessage() {
        viewModel.errorMessageProperty().set("Test error");
        assertEquals("Test error", viewModel.getErrorMessage());
    }
}
