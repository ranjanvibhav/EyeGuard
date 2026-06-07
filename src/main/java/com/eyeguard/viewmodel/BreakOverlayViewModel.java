package com.eyeguard.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the Break Overlay window.
 * Exposes observable properties representing the current state of the break countdown.
 */
public class BreakOverlayViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(BreakOverlayViewModel.class);

    private static final String INITIAL_COUNTDOWN_TEXT = "20";
    private static final double INITIAL_RING_PROGRESS = 1.0;
    private static final boolean INITIAL_DONE_BUTTON_ENABLED = false;
    private static final String INITIAL_ERROR_MESSAGE = "";

    private final StringProperty countdownText = new SimpleStringProperty(INITIAL_COUNTDOWN_TEXT);
    private final DoubleProperty ringProgress = new SimpleDoubleProperty(INITIAL_RING_PROGRESS);
    private final BooleanProperty doneButtonEnabled = new SimpleBooleanProperty(INITIAL_DONE_BUTTON_ENABLED);
    private final StringProperty errorMessage = new SimpleStringProperty(INITIAL_ERROR_MESSAGE);

    private Runnable onDone;
    private Runnable onSnooze;

    /**
     * Constructs the BreakOverlayViewModel and logs initialization.
     */
    public BreakOverlayViewModel() {
        LOGGER.debug("BreakOverlayViewModel initialized with default placeholder values");
    }

    /**
     * Sets the callback action for when the Done action is clicked.
     *
     * @param onDone the runnable callback
     */
    public void setOnDone(final Runnable onDone) {
        this.onDone = onDone;
    }

    /**
     * Gets the callback action for when the Done action is clicked.
     *
     * @return the runnable callback
     */
    public Runnable getOnDone() {
        return onDone;
    }

    /**
     * Sets the callback action for when the Snooze action is clicked.
     *
     * @param onSnooze the runnable callback
     */
    public void setOnSnooze(final Runnable onSnooze) {
        this.onSnooze = onSnooze;
    }

    /**
     * Gets the callback action for when the Snooze action is clicked.
     *
     * @return the runnable callback
     */
    public Runnable getOnSnooze() {
        return onSnooze;
    }

    /**
     * Gets the countdown text property.
     *
     * @return the countdown text property
     */
    public StringProperty countdownTextProperty() {
        return countdownText;
    }

    /**
     * Gets the current value of the countdown text.
     *
     * @return the countdown text string
     */
    public String getCountdownText() {
        return countdownText.get();
    }

    /**
     * Gets the ring progress property.
     *
     * @return the ring progress property
     */
    public DoubleProperty ringProgressProperty() {
        return ringProgress;
    }

    /**
     * Gets the current value of the ring progress.
     *
     * @return the progress double ratio between 0.0 and 1.0
     */
    public double getRingProgress() {
        return ringProgress.get();
    }

    /**
     * Gets the done button enabled state property.
     *
     * @return the done button enabled property
     */
    public BooleanProperty doneButtonEnabledProperty() {
        return doneButtonEnabled;
    }

    /**
     * Gets the current value of the done button enabled state.
     *
     * @return true if the done button should be enabled, false otherwise
     */
    public boolean isDoneButtonEnabled() {
        return doneButtonEnabled.get();
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
     * Gets the current value of the error message.
     *
     * @return the error message string
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }
}
