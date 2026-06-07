package com.eyeguard.service;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.Dimension;
import java.awt.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Windows-specific implementation of SystemFullscreenProvider using JNA EnumWindows.
 */
public class WindowsFullscreenProvider implements SystemFullscreenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsFullscreenProvider.class);

    @Override
    public boolean isFullscreenWindowPresent() {
        try {
            final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            final boolean[] result = new boolean[1];
            User32.INSTANCE.EnumWindows((hwnd, data) -> {
                if (checkWindow(hwnd, size.width, size.height)) {
                    result[0] = true;
                    return false;
                }
                return true;
            }, null);
            return result[0];
        } catch (final Exception e) {
            LOGGER.warn("Error checking Windows fullscreen status: {}", e.getMessage());
            return false;
        }
    }

    private boolean checkWindow(final HWND hwnd, final int screenWidth, final int screenHeight) {
        if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
            return false;
        }
        final RECT rect = new RECT();
        if (!User32.INSTANCE.GetWindowRect(hwnd, rect)) {
            return false;
        }
        if ((rect.right - rect.left) < screenWidth || (rect.bottom - rect.top) < screenHeight) {
            return false;
        }
        final char[] buffer = new char[512];
        final int len = User32.INSTANCE.GetWindowText(hwnd, buffer, 512);
        final String title = new String(buffer, 0, len).trim();
        return !title.isEmpty()
                && !title.equals("Program Manager")
                && !title.equals("Windows Shell Experience Host");
    }
}
