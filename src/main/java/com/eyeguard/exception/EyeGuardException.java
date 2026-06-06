package com.eyeguard.exception;

/**
 * Base custom exception for the EyeGuard application.
 * All application-specific exceptions should extend this class.
 */
public class EyeGuardException extends RuntimeException {

    /**
     * Constructs a new EyeGuardException with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public EyeGuardException(final String message) {
        super(message);
    }

    /**
     * Constructs a new EyeGuardException with the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the exception
     */
    public EyeGuardException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
