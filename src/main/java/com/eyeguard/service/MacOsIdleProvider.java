package com.eyeguard.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * macOS-specific implementation of SystemIdleProvider using ProcessBuilder to query ioreg.
 */
public class MacOsIdleProvider implements SystemIdleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacOsIdleProvider.class);

    @Override
    public long getIdleTimeSeconds() {
        try {
            final ProcessBuilder pb = new ProcessBuilder("bash", "-c", "ioreg -c IOHIDSystem | grep HIDIdleTime");
            final Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final long idleTime = parseIdleTimeLine(line);
                    if (idleTime > 0) {
                        return idleTime;
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.warn("Failed to retrieve macOS idle time: {}", e.getMessage());
        }
        return 0;
    }

    private long parseIdleTimeLine(final String line) {
        if (line.contains("HIDIdleTime")) {
            final String[] parts = line.split("=");
            if (parts.length > 1) {
                final String timeStr = parts[1].trim().replaceAll("[^0-9]", "");
                if (!timeStr.isEmpty()) {
                    return Long.parseLong(timeStr) / 1_000_000_000L;
                }
            }
        }
        return 0;
    }
}
