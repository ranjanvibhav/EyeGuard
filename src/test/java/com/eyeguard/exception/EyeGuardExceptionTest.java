package com.eyeguard.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the custom exception class {@link EyeGuardException}.
 */
class EyeGuardExceptionTest {

    private static final String TEST_MESSAGE = "Test error message";
    private static final String TEST_MESSAGE_WITH_CAUSE = "Test error message with cause";
    private static final String CAUSE_MESSAGE = "Invalid argument";

    /**
     * Verifies that the constructor accepting a message correctly initializes the exception.
     */
    @Test
    void testExceptionWithMessage() {
        final EyeGuardException exception = new EyeGuardException(TEST_MESSAGE);

        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Verifies that the constructor accepting both message and cause correctly initializes the exception.
     */
    @Test
    void testExceptionWithMessageAndCause() {
        final Throwable cause = new IllegalArgumentException(CAUSE_MESSAGE);
        final EyeGuardException exception = new EyeGuardException(TEST_MESSAGE_WITH_CAUSE, cause);

        assertEquals(TEST_MESSAGE_WITH_CAUSE, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
