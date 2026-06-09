package com.eyeguard.exception;

/**
 * Exception thrown when saving application settings fails.
 */
public class SettingsSaveException extends EyeGuardException {

    /**
     * Constructs a new SettingsSaveException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public SettingsSaveException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SettingsSaveException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the exception
     */
    public SettingsSaveException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
