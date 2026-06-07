package com.eyeguard.viewmodel;

import com.eyeguard.model.TimerState;
import com.eyeguard.service.TimerService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the MainWindow.
 * Exposes observable properties representing the current presentation state of the dashboard.
 */
public class MainWindowViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowViewModel.class);

    private static final String INITIAL_COUNTDOWN = "19:47";
    private static final double INITIAL_PROGRESS = 0.85;
    private static final String INITIAL_STATUS_TEXT = "ACTIVE";
    private static final String INITIAL_STATUS_STYLE = "status-badge-active";
    private static final int INITIAL_BREAKS_TAKEN = 8;
    private static final int INITIAL_SNOOZED_COUNT = 2;
    private static final String INITIAL_COMPLIANCE = "100%";
    private static final String INITIAL_SESSION_DURATION = "Active for 2h 34m";
    private static final String INITIAL_STREAK = "Streak: 5 days";
    private static final String INITIAL_ERROR = "";

    private final StringProperty nextBreakCountdown = new SimpleStringProperty(INITIAL_COUNTDOWN);
    private final DoubleProperty timerProgress = new SimpleDoubleProperty(INITIAL_PROGRESS);
    private final StringProperty statusText = new SimpleStringProperty(INITIAL_STATUS_TEXT);
    private final StringProperty statusStyle = new SimpleStringProperty(INITIAL_STATUS_STYLE);
    private final IntegerProperty breaksTaken = new SimpleIntegerProperty(INITIAL_BREAKS_TAKEN);
    private final IntegerProperty snoozedCount = new SimpleIntegerProperty(INITIAL_SNOOZED_COUNT);
    private final StringProperty compliancePercent = new SimpleStringProperty(INITIAL_COMPLIANCE);
    private final StringProperty sessionDuration = new SimpleStringProperty(INITIAL_SESSION_DURATION);
    private final StringProperty streakText = new SimpleStringProperty(INITIAL_STREAK);
    private final StringProperty errorMessage = new SimpleStringProperty(INITIAL_ERROR);

    /**
     * Constructs the MainWindowViewModel and logs initialization.
     */
    public MainWindowViewModel() {
        LOGGER.debug("MainWindowViewModel initialized with default placeholder values");
    }

    /**
     * Constructs the MainWindowViewModel bound to the TimerService properties.
     *
     * @param timerService the core countdown timer service
     */
    public MainWindowViewModel(final TimerService timerService) {
        nextBreakCountdown.bind(timerService.countdownTextProperty());
        timerProgress.bind(timerService.progressProperty());
        timerService.timerStateProperty().addListener((obs, oldState, newState) -> {
            updateStatusFromState(newState);
        });
        updateStatusFromState(timerService.getTimerState());
    }

    private void updateStatusFromState(final TimerState state) {
        switch (state) {
            case RUNNING -> {
                statusText.set("ACTIVE");
                statusStyle.set("status-badge-active");
            }
            case PAUSED -> {
                statusText.set("PAUSED");
                statusStyle.set("status-badge-paused");
            }
            case BREAK_DUE -> {
                statusText.set("BREAK!");
                statusStyle.set("status-badge-warning");
            }
            case STOPPED -> {
                statusText.set("STOPPED");
                statusStyle.set("status-badge-stopped");
            }
            case IDLE -> {
                statusText.set("IDLE");
                statusStyle.set("status-badge-stopped");
            }
        }
    }

    /**
     * Gets the next break countdown property.
     *
     * @return the countdown string property
     */
    public StringProperty nextBreakCountdownProperty() {
        return nextBreakCountdown;
    }

    /**
     * Gets the current value of the next break countdown.
     *
     * @return the countdown string
     */
    public String getNextBreakCountdown() {
        return nextBreakCountdown.get();
    }

    /**
     * Gets the timer progress property.
     *
     * @return the progress double property
     */
    public DoubleProperty timerProgressProperty() {
        return timerProgress;
    }

    /**
     * Gets the current value of the timer progress.
     *
     * @return the progress double between 0.0 and 1.0
     */
    public double getTimerProgress() {
        return timerProgress.get();
    }

    /**
     * Gets the status text property.
     *
     * @return the status text property
     */
    public StringProperty statusTextProperty() {
        return statusText;
    }

    /**
     * Gets the current value of the status text.
     *
     * @return the status text string
     */
    public String getStatusText() {
        return statusText.get();
    }

    /**
     * Gets the status style property (CSS class name).
     *
     * @return the status style property
     */
    public StringProperty statusStyleProperty() {
        return statusStyle;
    }

    /**
     * Gets the current value of the status style class name.
     *
     * @return the status style class name
     */
    public String getStatusStyle() {
        return statusStyle.get();
    }

    /**
     * Gets the breaks taken count property.
     *
     * @return the breaks taken integer property
     */
    public IntegerProperty breaksTakenProperty() {
        return breaksTaken;
    }

    /**
     * Gets the current value of the breaks taken count.
     *
     * @return the breaks taken count
     */
    public int getBreaksTaken() {
        return breaksTaken.get();
    }

    /**
     * Gets the snoozed count property.
     *
     * @return the snoozed count integer property
     */
    public IntegerProperty snoozedCountProperty() {
        return snoozedCount;
    }

    /**
     * Gets the current value of the snoozed count.
     *
     * @return the snoozed count
     */
    public int getSnoozedCount() {
        return snoozedCount.get();
    }

    /**
     * Gets the compliance percentage property.
     *
     * @return the compliance percentage string property
     */
    public StringProperty compliancePercentProperty() {
        return compliancePercent;
    }

    /**
     * Gets the current value of the compliance percentage.
     *
     * @return the compliance percentage string
     */
    public String getCompliancePercent() {
        return compliancePercent.get();
    }

    /**
     * Gets the session duration text property.
     *
     * @return the session duration string property
     */
    public StringProperty sessionDurationProperty() {
        return sessionDuration;
    }

    /**
     * Gets the current value of the session duration text.
     *
     * @return the session duration text
     */
    public String getSessionDuration() {
        return sessionDuration.get();
    }

    /**
     * Gets the streak text property.
     *
     * @return the streak text string property
     */
    public StringProperty streakTextProperty() {
        return streakText;
    }

    /**
     * Gets the current value of the streak text.
     *
     * @return the streak text
     */
    public String getStreakText() {
        return streakText.get();
    }

    /**
     * Gets the error message property.
     *
     * @return the error message string property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the current value of the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }
}
