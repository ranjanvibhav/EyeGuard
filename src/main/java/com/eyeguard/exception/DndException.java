package com.eyeguard.exception;

/**
 * Custom runtime exception representing errors in the DND service operations.
 */
public class DndException extends EyeGuardException {

    /**
     * Constructs a new DndException with the specified detail message.
     *
     * @param message the detail message
     */
    public DndException(final String message) {
        super(message);
    }

    /**
     * Constructs a new DndException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public DndException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
