package com.eyeguard.exception;

/**
 * Exception thrown when loading application settings fails.
 */
public class SettingsLoadException extends EyeGuardException {

    /**
     * Constructs a new SettingsLoadException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public SettingsLoadException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SettingsLoadException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the exception
     */
    public SettingsLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
