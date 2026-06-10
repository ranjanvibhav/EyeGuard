package com.eyeguard.service;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.URISyntaxException;

/**
 * Windows-specific implementation of StartupService.
 * Uses JNA Advapi32Util to write to Windows Registry HKCU\Software\Microsoft\Windows\CurrentVersion\Run.
 */
public class WindowsStartupService implements StartupService {

    private static final Logger log = LoggerFactory.getLogger(WindowsStartupService.class);
    private static final String REG_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String KEY_NAME = "EyeGuard";

    @Override
    public void register() {
        try {
            final String command = getCommand();
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, REG_PATH, KEY_NAME, command);
            log.info("Registered startup command: {}", command);
        } catch (final Exception e) {
            log.error("Failed to register startup command", e);
        }
    }

    @Override
    public void unregister() {
        try {
            if (isRegistered()) {
                Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, REG_PATH, KEY_NAME);
                log.info("Unregistered startup command");
            }
        } catch (final Exception e) {
            log.error("Failed to unregister startup command", e);
        }
    }

    @Override
    public boolean isRegistered() {
        try {
            return Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, REG_PATH, KEY_NAME);
        } catch (final Exception e) {
            log.error("Failed to check startup registration status", e);
            return false;
        }
    }

    private String getCommand() throws URISyntaxException {
        final String procCmd = ProcessHandle.current().info().command().orElse("");
        if (!procCmd.isEmpty() && procCmd.endsWith(".exe") && !procCmd.toLowerCase().contains("java")) {
            return "\"" + procCmd + "\" --startup";
        }
        final var cs = WindowsStartupService.class.getProtectionDomain().getCodeSource();
        if (cs != null && cs.getLocation() != null) {
            final File jarFile = new File(cs.getLocation().toURI());
            final File installDir = jarFile.getName().endsWith(".jar") ? jarFile.getParentFile().getParentFile() : null;
            final File exeFile = installDir != null ? new File(installDir, "EyeGuard.exe") : null;
            if (exeFile != null && exeFile.exists()) {
                return "\"" + exeFile.getAbsolutePath() + "\" --startup";
            }
        }
        final String javaw = System.getProperty("java.home") + File.separator + "bin" + File.separator + "javaw.exe";
        return "\"" + javaw + "\" -cp \"" + System.getProperty("java.class.path") + "\" com.eyeguard.app.Launcher --startup";
    }
}
