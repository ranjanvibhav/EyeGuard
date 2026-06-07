package com.eyeguard.util;

/**
 * Utility class for formatting seconds to user-readable time strings and progress ratios.
 */
public final class TimeFormatter {

    private TimeFormatter() {
        // Prevent instantiation
    }

    /**
     * Formats total seconds to a string representation in "MM:SS" format.
     *
     * @param totalSeconds the number of seconds to format
     * @return the formatted time string (e.g. "19:47")
     * @throws IllegalArgumentException if totalSeconds is negative
     */
    public static String formatSeconds(final int totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Seconds cannot be negative: " + totalSeconds);
        }
        final int minutes = totalSeconds / 60;
        final int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Calculates the remaining progress ratio, clamped between 0.0 and 1.0.
     *
     * @param remainingSeconds the remaining seconds left in the timer
     * @param totalSeconds     the total duration in seconds of the timer interval
     * @return the progress ratio between 0.0 and 1.0
     */
    public static double calculateProgress(final int remainingSeconds, final int totalSeconds) {
        if (totalSeconds <= 0) {
            return 1.0;
        }
        if (remainingSeconds <= 0) {
            return 0.0;
        }
        final double progress = remainingSeconds / (double) totalSeconds;
        return Math.max(0.0, Math.min(1.0, progress));
    }
}
