package com.eyeguard.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link TimeFormatter} utility class.
 */
class TimeFormatterTest {

    /**
     * Verifies that formatSeconds formats various positive integers correctly to MM:SS.
     */
    @Test
    void testFormatSeconds() {
        assertEquals("20:00", TimeFormatter.formatSeconds(1200));
        assertEquals("19:47", TimeFormatter.formatSeconds(1187));
        assertEquals("01:05", TimeFormatter.formatSeconds(65));
        assertEquals("00:09", TimeFormatter.formatSeconds(9));
        assertEquals("00:00", TimeFormatter.formatSeconds(0));
        assertEquals("60:00", TimeFormatter.formatSeconds(3600));
    }

    /**
     * Verifies that formatSeconds throws an IllegalArgumentException for negative inputs.
     */
    @Test
    void testFormatSecondsNegativeInput() {
        assertThrows(IllegalArgumentException.class, () -> TimeFormatter.formatSeconds(-1));
    }

    /**
     * Verifies that calculateProgress returns correct ratio values and clamps inputs.
     */
    @Test
    void testCalculateProgress() {
        assertEquals(1.0, TimeFormatter.calculateProgress(1200, 1200));
        assertEquals(0.0, TimeFormatter.calculateProgress(0, 1200));
        assertEquals(0.5, TimeFormatter.calculateProgress(600, 1200));
        assertEquals(1.0, TimeFormatter.calculateProgress(0, 0));
        assertEquals(0.0, TimeFormatter.calculateProgress(-1, 1200));
        assertEquals(1.0, TimeFormatter.calculateProgress(1300, 1200));
    }
}
