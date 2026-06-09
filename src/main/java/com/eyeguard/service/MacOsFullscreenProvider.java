package com.eyeguard.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * macOS-specific implementation of SystemFullscreenProvider using ProcessBuilder and AppleScript.
 */
public class MacOsFullscreenProvider implements SystemFullscreenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacOsFullscreenProvider.class);

    @Override
    public boolean isFullscreenWindowPresent() {
        final String script = "tell application \"System Events\" to get value of "
                + "attribute \"AXFullScreen\" of window 1 of "
                + "(first application process whose frontmost is true)";
        try {
            final Process p = new ProcessBuilder("osascript", "-e", script).start();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                final String line = reader.readLine();
                return line != null && line.trim().equals("true");
            }
        } catch (final Exception e) {
            LOGGER.warn("Failed to check macOS fullscreen status: {}", e.getMessage());
            return false;
        }
    }
}
