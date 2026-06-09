package com.eyeguard.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for custom settings exceptions to verify message and cause propagation.
 */
class SettingsExceptionsTest {

    /**
     * Verifies that SettingsLoadException correctly stores message and cause.
     */
    @Test
    void testSettingsLoadException() {
        final Throwable cause = new RuntimeException("root cause");
        final SettingsLoadException ex1 = new SettingsLoadException("load error");
        final SettingsLoadException ex2 = new SettingsLoadException("load error with cause", cause);

        assertEquals("load error", ex1.getMessage());
        assertEquals("load error with cause", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }

    /**
     * Verifies that SettingsSaveException correctly stores message and cause.
     */
    @Test
    void testSettingsSaveException() {
        final Throwable cause = new RuntimeException("root cause");
        final SettingsSaveException ex1 = new SettingsSaveException("save error");
        final SettingsSaveException ex2 = new SettingsSaveException("save error with cause", cause);

        assertEquals("save error", ex1.getMessage());
        assertEquals("save error with cause", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }

    /**
     * Verifies that SettingsValidationException correctly stores message and cause.
     */
    @Test
    void testSettingsValidationException() {
        final Throwable cause = new RuntimeException("root cause");
        final SettingsValidationException ex1 = new SettingsValidationException("validation error");
        final SettingsValidationException ex2 = new SettingsValidationException("validation error with cause", cause);

        assertEquals("validation error", ex1.getMessage());
        assertEquals("validation error with cause", ex2.getMessage());
        assertSame(cause, ex2.getCause());
    }
}
