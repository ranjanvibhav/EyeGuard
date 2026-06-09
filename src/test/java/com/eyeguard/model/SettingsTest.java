package com.eyeguard.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Settings} model class.
 */
class SettingsTest {

    /**
     * Verifies that the default constructor sets all fields to their defined default values.
     */
    @Test
    void testDefaultConstructor() {
        final Settings settings = new Settings();
        assertEquals(20, settings.getReminderIntervalMinutes());
        assertEquals(20, settings.getBreakDurationSeconds());
        assertEquals(5, settings.getSnoozeDurationMinutes());
        assertTrue(settings.isWorkingHoursEnabled());
        assertEquals("09:00", settings.getWorkStartTime());
        assertEquals("19:00", settings.getWorkEndTime());
        assertFalse(settings.isWeekendRemindersEnabled());
        assertTrue(settings.isFullscreenPauseEnabled());
        assertTrue(settings.isIdleDetectionEnabled());
        assertTrue(settings.isSoundAlertsEnabled());
        assertFalse(settings.isLaunchAtStartupEnabled());
    }

    /**
     * Verifies that settings objects with identical values are equal and share the same hashcode.
     */
    @Test
    void testEqualityAndHashCode() {
        final Settings s1 = new Settings();
        final Settings s2 = new Settings();
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertEquals(s1, s1);
    }

    /**
     * Verifies that settings objects with different values are not equal.
     */
    @Test
    void testInequality() {
        final Settings s1 = new Settings();
        final Settings s2 = new Settings();
        s2.setReminderIntervalMinutes(15);
        assertNotEquals(s1, s2);
        assertNotEquals(s1, null);
        assertNotEquals(s1, "different type");
    }

    /**
     * Verifies that toString() returns a non-null and non-empty string.
     */
    @Test
    void testToString() {
        final Settings settings = new Settings();
        final String str = settings.toString();
        assertNotNull(str);
        assertFalse(str.isBlank());
    }
}
