package com.eyeguard.service;

/**
 * OS-specific provider interface for querying the current duration of system-wide inactivity.
 */
public interface SystemIdleProvider {

    /**
     * Queries the underlying operating system for the duration of user inactivity.
     *
     * @return the idle duration in seconds, or 0 if unable to retrieve
     */
    long getIdleTimeSeconds();
}
