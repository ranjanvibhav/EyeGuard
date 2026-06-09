package com.eyeguard.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the {@link DndState} enum.
 */
class DndStateTest {

    @Test
    void testEnumValues() {
        final DndState[] states = DndState.values();
        assertEquals(4, states.length);
        assertNotNull(DndState.valueOf("INACTIVE"));
        assertNotNull(DndState.valueOf("SNOOZED"));
        assertNotNull(DndState.valueOf("PAUSED"));
        assertNotNull(DndState.valueOf("MEETING_MODE"));
    }
}
