package com.eyeguard.service;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Wtsapi32;
import com.sun.jna.platform.win32.WinDef.ATOM;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Windows-specific monitor that listens for workstation lock and unlock events
 * using WTSRegisterSessionNotification.
 */
public class WindowsSessionLockMonitor implements WindowProc {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSessionLockMonitor.class);
    private static final int WM_WTSSESSION_CHANGE = 0x02B1;
    private static final int WTS_SESSION_LOCK = 0x7;
    private static final int WTS_SESSION_UNLOCK = 0x8;

    private final Consumer<Boolean> onLockStateChanged;
    private HWND hwnd;
    private volatile boolean running = false;

    public WindowsSessionLockMonitor(final Consumer<Boolean> onLockStateChanged) {
        this.onLockStateChanged = onLockStateChanged;
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        final Thread thread = new Thread(this::runMessageLoop, "eyeguard-session-lock-monitor");
        thread.setDaemon(true);
        thread.start();
    }

    private void runMessageLoop() {
        if (!createMonitorWindow()) {
            return;
        }
        Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hwnd, Wtsapi32.NOTIFY_FOR_THIS_SESSION);
        LOGGER.info("Session lock monitor window created and notification registered.");
        processMessages();
        cleanup();
    }

    private boolean createMonitorWindow() {
        final String className = "EyeGuardSessionLockMonitorClass";
        if (!registerWindowClass(className)) {
            return false;
        }
        hwnd = User32.INSTANCE.CreateWindowEx(
            0, className, "EyeGuardSessionLockMonitor", 0,
            0, 0, 0, 0, null, null, null, null
        );
        if (hwnd == null) {
            LOGGER.error("Failed to create monitor window. Error code: {}", Kernel32.INSTANCE.GetLastError());
            return false;
        }
        return true;
    }

    private boolean registerWindowClass(final String className) {
        final WNDCLASSEX wc = new WNDCLASSEX();
        wc.cbSize = wc.size();
        wc.lpfnWndProc = this;
        wc.lpszClassName = className;
        wc.hInstance = Kernel32.INSTANCE.GetModuleHandle(null);
        final ATOM atom = User32.INSTANCE.RegisterClassEx(wc);
        if ((atom == null || atom.intValue() == 0) && Kernel32.INSTANCE.GetLastError() != 1410) {
            LOGGER.error("Failed to register window class. Error code: {}", Kernel32.INSTANCE.GetLastError());
            return false;
        }
        return true;
    }

    private void processMessages() {
        final MSG msg = new MSG();
        while (running && User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }
    }

    @Override
    public LRESULT callback(final HWND hwnd, final int uMsg, final WPARAM wParam, final LPARAM lParam) {
        if (uMsg == WM_WTSSESSION_CHANGE) {
            final int code = wParam.intValue();
            if (code == WTS_SESSION_LOCK) {
                onLockStateChanged.accept(true);
            } else if (code == WTS_SESSION_UNLOCK) {
                onLockStateChanged.accept(false);
            }
        }
        return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
    }

    public void stop() {
        running = false;
        if (hwnd != null) {
            User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, new WPARAM(0), new LPARAM(0));
        }
    }

    private void cleanup() {
        if (hwnd != null) {
            Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hwnd);
            User32.INSTANCE.DestroyWindow(hwnd);
            hwnd = null;
        }
    }
}
