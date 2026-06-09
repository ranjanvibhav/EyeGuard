package com.eyeguard.service;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.StdCallLibrary;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Windows-specific implementation of SystemIdleProvider using JNA to invoke GetLastInputInfo.
 */
public class WindowsIdleProvider implements SystemIdleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsIdleProvider.class);

    @Override
    public long getIdleTimeSeconds() {
        try {
            final User32Ext.LASTINPUTINFO lii = new User32Ext.LASTINPUTINFO();
            if (!User32Ext.INSTANCE.GetLastInputInfo(lii)) {
                return 0;
            }
            final long idleMillis = Kernel32.INSTANCE.GetTickCount() - lii.dwTime;
            return idleMillis / 1000;
        } catch (final Exception e) {
            LOGGER.warn("Failed to retrieve Windows idle time: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Extended User32 library mapping for JNA.
     */
    public interface User32Ext extends StdCallLibrary {
        User32Ext INSTANCE = Native.load("user32", User32Ext.class);

        /**
         * Retrieves the time of the last input event.
         *
         * @param lii pointer to LASTINPUTINFO structure
         * @return true if successful, false otherwise
         */
        boolean GetLastInputInfo(LASTINPUTINFO lii);

        /**
         * Windows LASTINPUTINFO structure layout.
         */
        class LASTINPUTINFO extends Structure {
            /** Size of the structure in bytes. Must be set to 8. */
            public int cbSize = 8;
            /** Tick count when the last input event occurred. */
            public int dwTime;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("cbSize", "dwTime");
            }
        }
    }
}
