package com.eyeguard.service;

import com.eyeguard.util.TrayIconFactory;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of TrayService managing SystemTray interactions on AWT Event Dispatch Thread (EDT)
 * and marshalling callbacks to JavaFX Application Thread.
 */
public class TrayServiceImpl implements TrayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrayServiceImpl.class);
    private static final int TRAY_ICON_SIZE = 64;

    private final Runnable onOpenWindow;
    private final Runnable onOpenDashboard;
    private final Runnable onSnooze5;
    private final Runnable onSnooze10;
    private final Runnable onMeetingMode30;
    private final Runnable onMeetingMode60;
    private final Runnable onPauseResume;
    private final Runnable onOpenSettings;
    private final Runnable onQuit;

    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private MenuItem statusMenuItem;
    private MenuItem pauseMenuItem;

    /**
     * Constructs the TrayServiceImpl with the specified action callbacks (legacy constructor).
     *
     * @param onOpenWindow callback to show the main window
     * @param onOpenDashboard callback to show the dashboard popup
     * @param onSnooze callback to snooze reminders
     * @param onPauseResume callback to pause/resume reminders
     * @param onOpenSettings callback to show the settings window
     * @param onQuit callback to exit the application cleanly
     */
    public TrayServiceImpl(final Runnable onOpenWindow,
                           final Runnable onOpenDashboard,
                           final Runnable onSnooze,
                           final Runnable onPauseResume,
                           final Runnable onOpenSettings,
                           final Runnable onQuit) {
        this(onOpenWindow, onOpenDashboard, onSnooze, onSnooze, onSnooze, onSnooze, onPauseResume, onOpenSettings, onQuit);
    }

    /**
     * Constructs the TrayServiceImpl with all action callbacks.
     *
     * @param onOpenWindow callback to show the main window
     * @param onOpenDashboard callback to show the dashboard popup
     * @param onSnooze5 callback to snooze reminders for 5 minutes
     * @param onSnooze10 callback to snooze reminders for 10 minutes
     * @param onMeetingMode30 callback to enable meeting mode for 30 minutes
     * @param onMeetingMode60 callback to enable meeting mode for 60 minutes
     * @param onPauseResume callback to pause/resume reminders
     * @param onOpenSettings callback to show the settings window
     * @param onQuit callback to exit the application cleanly
     */
    public TrayServiceImpl(final Runnable onOpenWindow,
                           final Runnable onOpenDashboard,
                           final Runnable onSnooze5,
                           final Runnable onSnooze10,
                           final Runnable onMeetingMode30,
                           final Runnable onMeetingMode60,
                           final Runnable onPauseResume,
                           final Runnable onOpenSettings,
                           final Runnable onQuit) {
        this.onOpenWindow = onOpenWindow;
        this.onOpenDashboard = onOpenDashboard;
        this.onSnooze5 = onSnooze5;
        this.onSnooze10 = onSnooze10;
        this.onMeetingMode30 = onMeetingMode30;
        this.onMeetingMode60 = onMeetingMode60;
        this.onPauseResume = onPauseResume;
        this.onOpenSettings = onOpenSettings;
        this.onQuit = onQuit;
    }

    /**
     * Initializes AWT SystemTray and builds the popup context menu.
     * Must be called on AWT Event Dispatch Thread.
     */
    @Override
    public void initializeTray() {
        runOnAwtThread(() -> {
            if (!SystemTray.isSupported()) {
                LOGGER.warn("SystemTray is not supported on this platform");
                return;
            }

            systemTray = SystemTray.getSystemTray();
            final Image image = TrayIconFactory.createTrayIconImage(TRAY_ICON_SIZE);

            final PopupMenu popupMenu = new PopupMenu();

            statusMenuItem = new MenuItem("Next break in: 19:47");
            statusMenuItem.setEnabled(false);
            statusMenuItem.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            popupMenu.add(statusMenuItem);

            popupMenu.addSeparator();

            final MenuItem openItem = new MenuItem("Open EyeGuard");
            openItem.addActionListener(e -> Platform.runLater(onOpenWindow));
            popupMenu.add(openItem);

            final MenuItem dashboardItem = new MenuItem("Dashboard");
            dashboardItem.addActionListener(e -> Platform.runLater(onOpenDashboard));
            popupMenu.add(dashboardItem);

            popupMenu.addSeparator();

            final MenuItem snoozeItem = new MenuItem("Snooze 5 minutes");
            snoozeItem.addActionListener(e -> Platform.runLater(onSnooze5));
            popupMenu.add(snoozeItem);

            final MenuItem snooze10Item = new MenuItem("Snooze 10 minutes");
            snooze10Item.addActionListener(e -> Platform.runLater(onSnooze10));
            popupMenu.add(snooze10Item);

            final MenuItem meeting30Item = new MenuItem("Meeting Mode (30 min)");
            meeting30Item.addActionListener(e -> Platform.runLater(onMeetingMode30));
            popupMenu.add(meeting30Item);

            final MenuItem meeting60Item = new MenuItem("Meeting Mode (60 min)");
            meeting60Item.addActionListener(e -> Platform.runLater(onMeetingMode60));
            popupMenu.add(meeting60Item);

            pauseMenuItem = new MenuItem("Pause Reminders");
            pauseMenuItem.addActionListener(e -> Platform.runLater(onPauseResume));
            popupMenu.add(pauseMenuItem);

            popupMenu.addSeparator();

            final MenuItem settingsItem = new MenuItem("Settings");
            settingsItem.addActionListener(e -> Platform.runLater(onOpenSettings));
            popupMenu.add(settingsItem);

            popupMenu.addSeparator();

            final MenuItem quitItem = new MenuItem("Quit EyeGuard");
            quitItem.addActionListener(e -> Platform.runLater(onQuit));
            popupMenu.add(quitItem);

            trayIcon = new TrayIcon(image, "EyeGuard — Next break in: 19:47", popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> Platform.runLater(onOpenDashboard));

            try {
                systemTray.add(trayIcon);
                LOGGER.info("System tray initialized");
            } catch (final Exception exception) {
                LOGGER.error("Failed to add TrayIcon to SystemTray", exception);
            }
        });
    }

    /**
     * Updates the tooltip displayed on the system tray icon.
     * Must be called on AWT EDT.
     *
     * @param text the new tooltip text
     */
    @Override
    public void updateTooltip(final String text) {
        runOnAwtThread(() -> {
            if (trayIcon != null) {
                trayIcon.setToolTip(text);
                LOGGER.debug("Tray tooltip updated: " + text);
            }
        });
    }

    /**
     * Updates the status menu item text.
     * Must be called on AWT EDT.
     *
     * @param text the new status label text
     */
    @Override
    public void updateStatusMenuItem(final String text) {
        runOnAwtThread(() -> {
            if (statusMenuItem != null) {
                statusMenuItem.setLabel(text);
                LOGGER.debug("Tray status item updated: " + text);
            }
        });
    }

    /**
     * Updates the pause/resume menu item text.
     * Must be called on AWT EDT.
     *
     * @param text the new pause/resume label text
     */
    public void updatePauseMenuItem(final String text) {
        runOnAwtThread(() -> {
            if (pauseMenuItem != null) {
                pauseMenuItem.setLabel(text);
                LOGGER.debug("Tray pause item updated: " + text);
            }
        });
    }

    /**
     * Removes the TrayIcon from the SystemTray on application shutdown.
     * Must be called on AWT EDT.
     */
    @Override
    public void dispose() {
        runOnAwtThread(() -> {
            if (systemTray != null && trayIcon != null) {
                systemTray.remove(trayIcon);
                trayIcon = null;
                LOGGER.info("System tray disposed");
            }
        });
    }

    /**
     * Helpers to execute task on the AWT Event Dispatch Thread.
     *
     * @param runnable task to execute
     */
    private void runOnAwtThread(final Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            EventQueue.invokeLater(runnable);
        }
    }
}
