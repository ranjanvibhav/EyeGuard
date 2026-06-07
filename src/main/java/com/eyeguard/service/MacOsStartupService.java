package com.eyeguard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;

/**
 * macOS-specific implementation of StartupService.
 * Writes a plist file to ~/Library/LaunchAgents/com.eyeguard.plist.
 */
public class MacOsStartupService implements StartupService {

    private static final Logger log = LoggerFactory.getLogger(MacOsStartupService.class);
    private static final String PLIST_NAME = "com.eyeguard.plist";

    private File getPlistFile() {
        final String home = System.getProperty("user.home");
        final File folder = new File(home, "Library/LaunchAgents");
        return new File(folder, PLIST_NAME);
    }

    @Override
    public void register() {
        try {
            final File file = getPlistFile();
            final File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (final FileWriter writer = new FileWriter(file)) {
                writer.write(getPlistContent());
            }
            log.info("Registered macOS startup plist: {}", file.getAbsolutePath());
        } catch (final Exception e) {
            log.error("Failed to register macOS startup plist", e);
        }
    }

    @Override
    public void unregister() {
        try {
            final File file = getPlistFile();
            if (file.exists() && file.delete()) {
                log.info("Unregistered macOS startup plist");
            }
        } catch (final Exception e) {
            log.error("Failed to unregister macOS startup plist", e);
        }
    }

    @Override
    public boolean isRegistered() {
        return getPlistFile().exists();
    }

    private String getPlistContent() throws URISyntaxException {
        final var cs = MacOsStartupService.class.getProtectionDomain().getCodeSource();
        final String path = (cs != null && cs.getLocation() != null)
                ? new File(cs.getLocation().toURI()).getAbsolutePath()
                : new File("eyeguard.jar").getAbsolutePath();
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
          .append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" ")
          .append("\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n")
          .append("<plist version=\"1.0\">\n<dict>\n")
          .append("  <key>Label</key>\n  <string>com.eyeguard</string>\n")
          .append("  <key>ProgramArguments</key>\n  <array>\n")
          .append("    <string>").append(javaBin).append("</string>\n");
        appendArgs(sb, path);
        sb.append("  </array>\n  <key>RunAtLoad</key>\n  <true/>\n</dict>\n</plist>");
        return sb.toString();
    }

    private void appendArgs(final StringBuilder sb, final String path) {
        if (path.endsWith(".jar")) {
            sb.append("    <string>-jar</string>\n")
              .append("    <string>").append(path).append("</string>\n");
        } else {
            final String cp = System.getProperty("java.class.path");
            sb.append("    <string>-cp</string>\n")
              .append("    <string>").append(cp).append("</string>\n")
              .append("    <string>com.eyeguard.app.Launcher</string>\n");
        }
    }
}
