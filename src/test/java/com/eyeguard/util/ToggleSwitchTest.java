package com.eyeguard.util;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link ToggleSwitch} custom UI control.
 */
class ToggleSwitchTest {

    /**
     * Initializes the JavaFX Toolkit runtime required for ToggleSwitch instantiation.
     */
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Toolkit already initialized, safe to ignore
        }
        Platform.setImplicitExit(false);
    }

    /**
     * Verifies that the initial selected state matches the constructor argument.
     */
    @Test
    void testInitialSelectedState() {
        final ToggleSwitch toggleOff = new ToggleSwitch(false);
        assertFalse(toggleOff.isSelected());
        assertFalse(toggleOff.selectedProperty().get());

        final ToggleSwitch toggleOn = new ToggleSwitch(true);
        assertTrue(toggleOn.isSelected());
        assertTrue(toggleOn.selectedProperty().get());
    }

    /**
     * Verifies that setSelected(true) updates the state correctly.
     */
    @Test
    void testSetSelectedTrue() {
        final ToggleSwitch toggle = new ToggleSwitch(false);
        toggle.setSelected(true);
        assertTrue(toggle.isSelected());
        assertTrue(toggle.selectedProperty().get());
    }

    /**
     * Verifies that setSelected(false) updates the state correctly.
     */
    @Test
    void testSetSelectedFalse() {
        final ToggleSwitch toggle = new ToggleSwitch(true);
        toggle.setSelected(false);
        assertFalse(toggle.isSelected());
        assertFalse(toggle.selectedProperty().get());
    }

    /**
     * Verifies that the selectedProperty fires change events when the value is modified.
     */
    @Test
    void testSelectedPropertyFiresChangeEvent() {
        final ToggleSwitch toggle = new ToggleSwitch(false);
        final boolean[] eventFired = {false};

        toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                eventFired[0] = true;
            }
        });

        toggle.setSelected(true);
        assertTrue(eventFired[0], "Change listener event was not fired upon state modification");
    }
}
