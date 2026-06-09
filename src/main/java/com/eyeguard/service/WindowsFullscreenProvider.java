package com.eyeguard.service;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.Dimension;
import java.awt.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Windows-specific implementation of SystemFullscreenProvider using JNA GetForegroundWindow.
 */
public class WindowsFullscreenProvider implements SystemFullscreenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsFullscreenProvider.class);

    /**
     * Checks if the foreground window is currently running in fullscreen mode.
     *
     * @return true if a fullscreen window is in the foreground, false otherwise
     */
    @Override
    public boolean isFullscreenWindowPresent() {
        try {
            final HWND fg = User32.INSTANCE.GetForegroundWindow();
            if (fg == null) {
                return false;
            }
            final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            return checkWindow(fg, size.width, size.height);
        } catch (final Exception e) {
            LOGGER.warn("Error checking Windows fullscreen status: {}", e.getMessage());
            return false;
        }
    }

    private boolean checkWindow(final HWND hwnd, final int w, final int h) {
        final RECT r = new RECT();
        if (!User32.INSTANCE.GetWindowRect(hwnd, r) || !User32.INSTANCE.IsWindowVisible(hwnd)) {
            return false;
        }
        final int style = User32.INSTANCE.GetWindowLong(hwnd, -16);
        if ((style & 0x00C00000) == 0x00C00000) {
            return false;
        }
        final char[] buf = new char[512];
        final int len = User32.INSTANCE.GetWindowText(hwnd, buf, 512);
        final String title = new String(buf, 0, len).trim();
        return (r.right - r.left) >= w && (r.bottom - r.top) >= h && !title.isEmpty()
                && !title.equals("Program Manager") && !title.equals("Windows Shell Experience Host");
    }
}
