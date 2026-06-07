package com.eyeguard.viewmodel;

import com.eyeguard.service.TimerService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the System Tray representation.
 * Manages tray-specific state properties and controls the Pause/Resume transition logic.
 */
public class TrayViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrayViewModel.class);

    private static final String INITIAL_TOOLTIP = "EyeGuard — Next break in: 19:47";
    private static final String INITIAL_STATUS = "Next break in: 19:47";
    private static final boolean INITIAL_PAUSED = false;
    private static final String INITIAL_PAUSE_LABEL = "Pause Reminders";
    private static final String LABEL_RESUME = "Resume Reminders";
    private static final String INITIAL_ERROR = "";

    private final StringProperty tooltipText = new SimpleStringProperty(INITIAL_TOOLTIP);
    private final StringProperty statusMenuItemText = new SimpleStringProperty(INITIAL_STATUS);
    private final BooleanProperty isPaused = new SimpleBooleanProperty(INITIAL_PAUSED);
    private final StringProperty pauseMenuItemText = new SimpleStringProperty(INITIAL_PAUSE_LABEL);
    private final StringProperty errorMessage = new SimpleStringProperty(INITIAL_ERROR);

    /**
     * Constructs the TrayViewModel and logs initialization.
     */
    public TrayViewModel() {
        LOGGER.debug("TrayViewModel initialized with default placeholder values");
    }

    /**
     * Constructs the TrayViewModel bound to the TimerService countdown ticks.
     *
     * @param timerService the core countdown timer service
     */
    public TrayViewModel(final TimerService timerService) {
        timerService.countdownTextProperty().addListener((obs, oldVal, newVal) -> {
            final String tooltip = "EyeGuard — Next break in: " + newVal;
            tooltipText.set(tooltip);
            statusMenuItemText.set("Next break in: " + newVal);
        });
    }

    /**
     * Toggles the pause state of reminders and updates the pause menu item text.
     */
    public void togglePause() {
        if (!isPaused.get()) {
            isPaused.set(true);
            pauseMenuItemText.set(LABEL_RESUME);
            LOGGER.info("Reminders paused");
        } else {
            isPaused.set(false);
            pauseMenuItemText.set(INITIAL_PAUSE_LABEL);
            LOGGER.info("Reminders resumed");
        }
    }

    /**
     * Gets the tooltip text property.
     *
     * @return the tooltip text property
     */
    public StringProperty tooltipTextProperty() {
        return tooltipText;
    }

    /**
     * Gets the current value of the tooltip text.
     *
     * @return the tooltip text string
     */
    public String getTooltipText() {
        return tooltipText.get();
    }

    /**
     * Gets the status menu item text property.
     *
     * @return the status menu item text property
     */
    public StringProperty statusMenuItemTextProperty() {
        return statusMenuItemText;
    }

    /**
     * Gets the current value of the status menu item text.
     *
     * @return the status menu item text string
     */
    public String getStatusMenuItemText() {
        return statusMenuItemText.get();
    }

    /**
     * Gets the isPaused state property.
     *
     * @return the isPaused boolean property
     */
    public BooleanProperty isPausedProperty() {
        return isPaused;
    }

    /**
     * Checks if reminders are currently paused.
     *
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return isPaused.get();
    }

    /**
     * Gets the pause menu item text property.
     *
     * @return the pause menu item text property
     */
    public StringProperty pauseMenuItemTextProperty() {
        return pauseMenuItemText;
    }

    /**
     * Gets the current value of the pause menu item text.
     *
     * @return the pause menu item text string
     */
    public String getPauseMenuItemText() {
        return pauseMenuItemText.get();
    }

    /**
     * Gets the error message property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the current error message.
     *
     * @return the error message string
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }
}
