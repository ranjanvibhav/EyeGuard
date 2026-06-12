package com.eyeguard.viewmodel;

import com.eyeguard.service.IdleDetectionService;
import com.eyeguard.service.TimerService;
import com.eyeguard.service.DndService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the Dashboard stats popup.
 * Manages properties representing break timers, compliance stats, session state, and error logs.
 */
public class DashboardViewModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardViewModel.class);

    private final StringProperty nextBreakCountdown = new SimpleStringProperty("19:47");
    private final DoubleProperty timerProgress = new SimpleDoubleProperty(0.85);
    private final StringProperty activeStatusText = new SimpleStringProperty("Active");
    private final StringProperty activeStatusStyle = new SimpleStringProperty("status-text-active");
    private final IntegerProperty breaksTaken = new SimpleIntegerProperty(8);
    private final IntegerProperty snoozedCount = new SimpleIntegerProperty(2);
    private final StringProperty compliancePercent = new SimpleStringProperty("80%");
    private final IntegerProperty streakDays = new SimpleIntegerProperty(5);
    private final StringProperty sessionDuration = new SimpleStringProperty("Active for 2h 34m");
    private final StringProperty idleStatusText = new SimpleStringProperty("● Tracking");
    private final StringProperty idleStatusStyle = new SimpleStringProperty("status-text-active");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private Runnable onSnoozeQuick;
    private Runnable onPauseQuick;
    private Runnable onSettingsQuick;

    /**
     * Constructs the DashboardViewModel.
     */
    public DashboardViewModel() {
        LOGGER.debug("DashboardViewModel initialized with default placeholder values");
    }

    /**
     * Constructs the DashboardViewModel bound to the IdleDetectionService status.
     *
     * @param idleDetectionService the inactivity monitoring service
     */
    public DashboardViewModel(final IdleDetectionService idleDetectionService) {
        idleDetectionService.isIdleProperty().addListener((obs, old, isNowIdle) -> {
            if (isNowIdle) {
                idleStatusText.set("● Idle");
                idleStatusStyle.set("status-text-idle");
            } else {
                idleStatusText.set("● Tracking");
                idleStatusStyle.set("status-text-active");
            }
        });
    }

    /**
     * Constructs the DashboardViewModel bound to the core timer, dnd, and idle services.
     *
     * @param timerService the core countdown timer service
     * @param dndService   the DND state coordination service
     * @param idleService  the inactivity monitoring service
     */
    public DashboardViewModel(final TimerService timerService,
                              final DndService dndService,
                              final IdleDetectionService idleService) {
        this(idleService);
        nextBreakCountdown.bind(timerService.countdownTextProperty());
        timerProgress.bind(timerService.progressProperty());
        dndService.dndStatusTextProperty().addListener((obs, old, newVal) -> updateStatus(timerService, dndService));
        timerService.timerStateProperty().addListener((obs, old, newVal) -> updateStatus(timerService, dndService));
        updateStatus(timerService, dndService);
    }

    /**
     * Master constructor including StatisticsService.
     */
    public DashboardViewModel(final TimerService timerService,
                              final DndService dndService,
                              final IdleDetectionService idleService,
                              final com.eyeguard.service.StatisticsService statisticsService) {
        this(timerService, dndService, idleService);
        bindStatistics(statisticsService);
    }

    private void updateIdleStatus(final boolean isNowIdle) {
        // Obsolete - functionality restored to original constructors
    }

    private void bindStatistics(final com.eyeguard.service.StatisticsService stats) {
        breaksTaken.set(stats.breaksTakenProperty().get());
        snoozedCount.set(stats.snoozedCountProperty().get());
        compliancePercent.set(stats.compliancePercentProperty().get());
        streakDays.set(stats.streakDaysProperty().get());
        sessionDuration.set(stats.sessionDurationProperty().get());

        stats.breaksTakenProperty().addListener((obs, old, newVal) -> breaksTaken.set(newVal.intValue()));
        stats.snoozedCountProperty().addListener((obs, old, newVal) -> snoozedCount.set(newVal.intValue()));
        stats.compliancePercentProperty().addListener((obs, old, newVal) -> compliancePercent.set(newVal));
        stats.streakDaysProperty().addListener((obs, old, newVal) -> streakDays.set(newVal.intValue()));
        stats.sessionDurationProperty().addListener((obs, old, newVal) -> sessionDuration.set(newVal));
    }

    private void updateStatus(final TimerService timer, final DndService dnd) {
        final com.eyeguard.model.DndState dndState = dnd.getDndState();
        if (dndState != com.eyeguard.model.DndState.INACTIVE) {
            activeStatusText.set(dndState.name());
            activeStatusStyle.set("status-text-idle");
            return;
        }
        switch (timer.getTimerState()) {
            case RUNNING -> setStatusState("Active", "status-text-active");
            case PAUSED -> setStatusState("Paused", "status-text-inactive");
            case BREAK_DUE -> setStatusState("Break!", "status-text-idle");
            default -> setStatusState(timer.getTimerState().name(), "status-text-inactive");
        }
    }

    private void setStatusState(final String text, final String style) {
        activeStatusText.set(text);
        activeStatusStyle.set(style);
    }

    /**
     * Sets the Runnable callback for quick snooze button action.
     *
     * @param callback the quick snooze callback
     */
    public void setOnSnoozeQuick(final Runnable callback) {
        this.onSnoozeQuick = callback;
    }

    /**
     * Gets the Runnable callback for quick snooze button action.
     *
     * @return the quick snooze callback
     */
    public Runnable getOnSnoozeQuick() {
        return onSnoozeQuick;
    }

    /**
     * Sets the Runnable callback for quick pause button action.
     *
     * @param callback the quick pause callback
     */
    public void setOnPauseQuick(final Runnable callback) {
        this.onPauseQuick = callback;
    }

    /**
     * Gets the Runnable callback for quick pause button action.
     *
     * @return the quick pause callback
     */
    public Runnable getOnPauseQuick() {
        return onPauseQuick;
    }

    /**
     * Sets the Runnable callback for quick settings button action.
     *
     * @param callback the quick settings callback
     */
    public void setOnSettingsQuick(final Runnable callback) {
        this.onSettingsQuick = callback;
    }

    /**
     * Gets the Runnable callback for quick settings button action.
     *
     * @return the quick settings callback
     */
    public Runnable getOnSettingsQuick() {
        return onSettingsQuick;
    }

    /**
     * Gets nextBreakCountdown property.
     *
     * @return nextBreakCountdown property
     */
    public StringProperty nextBreakCountdownProperty() {
        return nextBreakCountdown;
    }

    /**
     * Gets nextBreakCountdown value.
     *
     * @return nextBreakCountdown string
     */
    public String getNextBreakCountdown() {
        return nextBreakCountdown.get();
    }

    /**
     * Gets timerProgress property.
     *
     * @return timerProgress property
     */
    public DoubleProperty timerProgressProperty() {
        return timerProgress;
    }

    /**
     * Gets timerProgress value.
     *
     * @return timerProgress double
     */
    public double getTimerProgress() {
        return timerProgress.get();
    }

    /**
     * Gets activeStatusText property.
     *
     * @return activeStatusText property
     */
    public StringProperty activeStatusTextProperty() {
        return activeStatusText;
    }

    /**
     * Gets activeStatusText value.
     *
     * @return activeStatusText string
     */
    public String getActiveStatusText() {
        return activeStatusText.get();
    }

    /**
     * Gets activeStatusStyle property.
     *
     * @return activeStatusStyle property
     */
    public StringProperty activeStatusStyleProperty() {
        return activeStatusStyle;
    }

    /**
     * Gets activeStatusStyle value.
     *
     * @return activeStatusStyle string
     */
    public String getActiveStatusStyle() {
        return activeStatusStyle.get();
    }

    /**
     * Gets breaksTaken property.
     *
     * @return breaksTaken property
     */
    public IntegerProperty breaksTakenProperty() {
        return breaksTaken;
    }

    /**
     * Gets breaksTaken value.
     *
     * @return breaksTaken int
     */
    public int getBreaksTaken() {
        return breaksTaken.get();
    }

    /**
     * Gets snoozedCount property.
     *
     * @return snoozedCount property
     */
    public IntegerProperty snoozedCountProperty() {
        return snoozedCount;
    }

    /**
     * Gets snoozedCount value.
     *
     * @return snoozedCount int
     */
    public int getSnoozedCount() {
        return snoozedCount.get();
    }

    /**
     * Gets compliancePercent property.
     *
     * @return compliancePercent property
     */
    public StringProperty compliancePercentProperty() {
        return compliancePercent;
    }

    /**
     * Gets compliancePercent value.
     *
     * @return compliancePercent string
     */
    public String getCompliancePercent() {
        return compliancePercent.get();
    }

    /**
     * Gets streakDays property.
     *
     * @return streakDays property
     */
    public IntegerProperty streakDaysProperty() {
        return streakDays;
    }

    /**
     * Gets streakDays value.
     *
     * @return streakDays int
     */
    public int getStreakDays() {
        return streakDays.get();
    }

    /**
     * Gets sessionDuration property.
     *
     * @return sessionDuration property
     */
    public StringProperty sessionDurationProperty() {
        return sessionDuration;
    }

    /**
     * Gets sessionDuration value.
     *
     * @return sessionDuration string
     */
    public String getSessionDuration() {
        return sessionDuration.get();
    }

    /**
     * Gets idleStatusText property.
     *
     * @return idleStatusText property
     */
    public StringProperty idleStatusTextProperty() {
        return idleStatusText;
    }

    /**
     * Gets idleStatusText value.
     *
     * @return idleStatusText string
     */
    public String getIdleStatusText() {
        return idleStatusText.get();
    }

    /**
     * Gets idleStatusStyle property.
     *
     * @return idleStatusStyle property
     */
    public StringProperty idleStatusStyleProperty() {
        return idleStatusStyle;
    }

    /**
     * Gets idleStatusStyle value.
     *
     * @return idleStatusStyle string
     */
    public String getIdleStatusStyle() {
        return idleStatusStyle.get();
    }

    /**
     * Gets errorMessage property.
     *
     * @return errorMessage property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets errorMessage value.
     *
     * @return errorMessage string
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }
}
