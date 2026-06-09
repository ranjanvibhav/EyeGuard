package com.eyeguard.exception;

/**
 * Exception representing errors during timer configuration or execution.
 */
public class TimerException extends EyeGuardException {

    /**
     * Constructs a new TimerException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public TimerException(final String message) {
        super(message);
    }

    /**
     * Constructs a new TimerException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the exception
     */
    public TimerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
