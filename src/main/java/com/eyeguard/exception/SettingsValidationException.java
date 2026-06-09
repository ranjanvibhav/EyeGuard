package com.eyeguard.exception;

/**
 * Exception thrown when settings fail validation constraints.
 */
public class SettingsValidationException extends EyeGuardException {

    /**
     * Constructs a new SettingsValidationException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public SettingsValidationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SettingsValidationException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the exception
     */
    public SettingsValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
