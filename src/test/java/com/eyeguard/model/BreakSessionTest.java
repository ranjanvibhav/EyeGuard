package com.eyeguard.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for BreakSession model class.
 */
class BreakSessionTest {

    @Test
    void testConstructorInitialization() {
        final LocalDateTime now = LocalDateTime.now();
        final BreakSession session = new BreakSession(now, 20);
        assertEquals(now, session.getStartTime());
        assertEquals(20, session.getDurationSeconds());
        assertNull(session.getEndTime());
        assertFalse(session.isCompleted());
        assertFalse(session.isSnoozed());
        assertFalse(session.isFinished());
    }

    @Test
    void testCompleteSession() {
        final BreakSession session = new BreakSession(LocalDateTime.now(), 20);
        final LocalDateTime end = LocalDateTime.now();
        session.complete(end);
        assertEquals(end, session.getEndTime());
        assertTrue(session.isCompleted());
        assertFalse(session.isSnoozed());
        assertTrue(session.isFinished());
    }

    @Test
    void testCompleteThrowsIfFinished() {
        final BreakSession session = new BreakSession(LocalDateTime.now(), 20);
        session.complete(LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> session.complete(LocalDateTime.now()));
        assertThrows(IllegalStateException.class, () -> session.snooze(LocalDateTime.now()));
    }

    @Test
    void testSnoozeSession() {
        final BreakSession session = new BreakSession(LocalDateTime.now(), 20);
        final LocalDateTime end = LocalDateTime.now();
        session.snooze(end);
        assertEquals(end, session.getEndTime());
        assertFalse(session.isCompleted());
        assertTrue(session.isSnoozed());
        assertTrue(session.isFinished());
    }

    @Test
    void testSnoozeThrowsIfFinished() {
        final BreakSession session = new BreakSession(LocalDateTime.now(), 20);
        session.snooze(LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> session.snooze(LocalDateTime.now()));
        assertThrows(IllegalStateException.class, () -> session.complete(LocalDateTime.now()));
    }

    @Test
    void testToStringContainsKeyFields() {
        final BreakSession session = new BreakSession(LocalDateTime.now(), 20);
        final String str = session.toString();
        assertNotNull(str);
        assertTrue(str.contains("durationSeconds=20"));
        assertTrue(str.contains("completed=false"));
        assertTrue(str.contains("snoozed=false"));
    }
}
