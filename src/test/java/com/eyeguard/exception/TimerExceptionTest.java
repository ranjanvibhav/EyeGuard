package com.eyeguard.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for custom {@link TimerException} verifying message and cause propagation.
 */
class TimerExceptionTest {

    /**
     * Verifies that TimerException correctly stores message and cause.
     */
    @Test
    void testTimerException() {
        final Throwable cause = new RuntimeException("root cause");
        final TimerException ex1 = new TimerException("timer error");
        final TimerException ex2 = new TimerException("timer error with cause", cause);

        assertEquals("timer error", ex1.getMessage());
        assertEquals("timer error with cause", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }
}
