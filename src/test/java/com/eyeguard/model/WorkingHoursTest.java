package com.eyeguard.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link WorkingHours}.
 */
class WorkingHoursTest {

    @Test
    void testConstructorCreatesObject() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, false);
        assertNotNull(wh);
        assertEquals(LocalTime.of(9, 0), wh.getStartTime());
        assertEquals(LocalTime.of(17, 0), wh.getEndTime());
        assertTrue(wh.isEnabled());
        assertFalse(wh.isWeekendRemindersEnabled());
    }

    @Test
    void testConstructorThrowsOnEqualTimes() {
        assertThrows(IllegalArgumentException.class, () ->
            new WorkingHours(LocalTime.of(9, 0), LocalTime.of(9, 0), true, false));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void testConstructorThrowsOnStartAfterEnd() {
        assertThrows(IllegalArgumentException.class, () ->
            new WorkingHours(LocalTime.of(10, 0), LocalTime.of(9, 0), true, false));
    }

    @Test
    void testIsWithinWorkingHoursWeekdayInside() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, false);
        final LocalDateTime weekdayInside = LocalDateTime.of(2026, 6, 8, 12, 0); // Monday
        assertTrue(wh.isWithinWorkingHours(weekdayInside));
    }

    @Test
    void testIsWithinWorkingHoursWeekdayBefore() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, false);
        final LocalDateTime weekdayBefore = LocalDateTime.of(2026, 6, 8, 8, 59); // Monday
        assertFalse(wh.isWithinWorkingHours(weekdayBefore));
    }

    @Test
    void testIsWithinWorkingHoursWeekdayAfter() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, false);
        final LocalDateTime weekdayAfter = LocalDateTime.of(2026, 6, 8, 17, 0); // Monday
        assertFalse(wh.isWithinWorkingHours(weekdayAfter));
    }

    @Test
    void testIsWithinWorkingHoursSaturdayWeekendDisabled() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, false);
        final LocalDateTime saturday = LocalDateTime.of(2026, 6, 6, 12, 0); // Saturday
        assertFalse(wh.isWithinWorkingHours(saturday));
    }

    @Test
    void testIsWithinWorkingHoursSundayWeekendDisabled() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, false);
        final LocalDateTime sunday = LocalDateTime.of(2026, 6, 7, 12, 0); // Sunday
        assertFalse(wh.isWithinWorkingHours(sunday));
    }

    @Test
    void testIsWithinWorkingHoursSaturdayWeekendEnabled() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), true, true);
        final LocalDateTime saturdayInside = LocalDateTime.of(2026, 6, 6, 12, 0); // Saturday
        assertTrue(wh.isWithinWorkingHours(saturdayInside));
    }

    @Test
    void testIsWithinWorkingHoursDisabled() {
        final WorkingHours wh = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0), false, false);
        final LocalDateTime saturdayBefore = LocalDateTime.of(2026, 6, 6, 8, 0); // Saturday
        assertTrue(wh.isWithinWorkingHours(saturdayBefore));
    }

    @Test
    void testFromSettingsValid() {
        final Settings settings = new Settings();
        settings.setWorkStartTime("08:30");
        settings.setWorkEndTime("18:30");
        settings.setWorkingHoursEnabled(true);
        settings.setWeekendRemindersEnabled(true);
        final WorkingHours wh = WorkingHours.fromSettings(settings);
        assertEquals(LocalTime.of(8, 30), wh.getStartTime());
        assertEquals(LocalTime.of(18, 30), wh.getEndTime());
        assertTrue(wh.isEnabled());
        assertTrue(wh.isWeekendRemindersEnabled());
    }

    @Test
    void testFromSettingsInvalidTimeStrings() {
        final Settings settings = new Settings();
        settings.setWorkStartTime("invalid");
        settings.setWorkEndTime("19:00");
        final WorkingHours wh = WorkingHours.fromSettings(settings);
        assertEquals(LocalTime.of(9, 0), wh.getStartTime());
        assertEquals(LocalTime.of(19, 0), wh.getEndTime());
        assertTrue(wh.isEnabled());
        assertFalse(wh.isWeekendRemindersEnabled());
    }
}
