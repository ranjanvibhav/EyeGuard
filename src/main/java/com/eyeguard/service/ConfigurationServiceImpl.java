package com.eyeguard.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.eyeguard.exception.SettingsLoadException;
import com.eyeguard.exception.SettingsSaveException;
import com.eyeguard.exception.SettingsValidationException;
import com.eyeguard.model.Settings;
import com.eyeguard.model.SettingsConstraints;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ConfigurationService utilizing Jackson ObjectMapper for JSON persistence on disk.
 */
public class ConfigurationServiceImpl implements ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");

    private final ObjectMapper objectMapper;
    private final Path settingsFilePath;

    /**
     * Constructs a ConfigurationServiceImpl with the specified file path.
     *
     * @param settingsFilePath the file path on disk to load/save settings
     */
    public ConfigurationServiceImpl(final Path settingsFilePath) {
        this.settingsFilePath = settingsFilePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Factory method creating a ConfigurationServiceImpl targeting user home directory path.
     *
     * @return a default ConfigurationServiceImpl instance
     */
    public static ConfigurationServiceImpl createDefault() {
        return new ConfigurationServiceImpl(Path.of(System.getProperty("user.home"),
                SettingsConstraints.SETTINGS_DIRECTORY_NAME,
                SettingsConstraints.SETTINGS_FILE_NAME));
    }

    @Override
    public Settings loadSettings() throws SettingsLoadException {
        if (!Files.exists(settingsFilePath)) {
            LOGGER.info("Settings file not found, using defaults: {}", settingsFilePath);
            return getDefaultSettings();
        }
        try {
            final Settings settings = objectMapper.readValue(settingsFilePath.toFile(), Settings.class);
            LOGGER.info("Settings loaded from {}", settingsFilePath);
            return settings;
        } catch (final com.fasterxml.jackson.core.JsonProcessingException e) {
            LOGGER.error("Settings file is corrupted: {}", settingsFilePath, e);
            throw new SettingsLoadException("Settings file is corrupted. Default settings will be used.", e);
        } catch (final IOException e) {
            LOGGER.error("Failed to read settings from: {}", settingsFilePath, e);
            throw new SettingsLoadException("Failed to read settings from " + settingsFilePath, e);
        }
    }

    @Override
    public void saveSettings(final Settings settings) throws SettingsSaveException, SettingsValidationException {
        validateSettings(settings);
        try {
            if (settingsFilePath.getParent() != null) {
                Files.createDirectories(settingsFilePath.getParent());
            }
            objectMapper.writeValue(settingsFilePath.toFile(), settings);
            LOGGER.info("Settings saved to {}", settingsFilePath);
        } catch (final com.fasterxml.jackson.core.JsonProcessingException e) {
            LOGGER.error("Failed to serialize settings", e);
            throw new SettingsSaveException("Failed to serialize settings as JSON", e);
        } catch (final IOException e) {
            LOGGER.error("Failed to write settings file to {}", settingsFilePath, e);
            throw new SettingsSaveException("Failed to write settings file to " + settingsFilePath, e);
        }
    }

    @Override
    public Settings getDefaultSettings() {
        return new Settings();
    }

    @Override
    public void validateSettings(final Settings settings) throws SettingsValidationException {
        validateNumericIntervals(settings);
        validateWorkTimes(settings);
    }

    private void validateNumericIntervals(final Settings settings) throws SettingsValidationException {
        if (settings.getReminderIntervalMinutes() < SettingsConstraints.MIN_REMINDER_INTERVAL_MINUTES
                || settings.getReminderIntervalMinutes() > SettingsConstraints.MAX_REMINDER_INTERVAL_MINUTES) {
            final String message = "Reminder interval must be between "
                    + SettingsConstraints.MIN_REMINDER_INTERVAL_MINUTES + " and "
                    + SettingsConstraints.MAX_REMINDER_INTERVAL_MINUTES + " minutes.";
            LOGGER.warn(message);
            throw new SettingsValidationException(message);
        }
        if (settings.getBreakDurationSeconds() < SettingsConstraints.MIN_BREAK_DURATION_SECONDS
                || settings.getBreakDurationSeconds() > SettingsConstraints.MAX_BREAK_DURATION_SECONDS) {
            final String message = "Break duration must be between "
                    + SettingsConstraints.MIN_BREAK_DURATION_SECONDS + " and "
                    + SettingsConstraints.MAX_BREAK_DURATION_SECONDS + " seconds.";
            LOGGER.warn(message);
            throw new SettingsValidationException(message);
        }
        validateSnoozeInterval(settings);
    }

    private void validateSnoozeInterval(final Settings settings) throws SettingsValidationException {
        if (settings.getSnoozeDurationMinutes() < SettingsConstraints.MIN_SNOOZE_DURATION_MINUTES
                || settings.getSnoozeDurationMinutes() > SettingsConstraints.MAX_SNOOZE_DURATION_MINUTES) {
            final String message = "Snooze duration must be between "
                    + SettingsConstraints.MIN_SNOOZE_DURATION_MINUTES + " and "
                    + SettingsConstraints.MAX_SNOOZE_DURATION_MINUTES + " minutes.";
            LOGGER.warn(message);
            throw new SettingsValidationException(message);
        }
    }

    private void validateWorkTimes(final Settings settings) throws SettingsValidationException {
        final String start = settings.getWorkStartTime();
        final String end = settings.getWorkEndTime();
        if (start == null || start.isBlank()) {
            LOGGER.warn("Work start time is null or blank");
            throw new SettingsValidationException("Work start time cannot be empty.");
        }
        if (end == null || end.isBlank()) {
            LOGGER.warn("Work end time is null or blank");
            throw new SettingsValidationException("Work end time cannot be empty.");
        }
        if (!TIME_PATTERN.matcher(start).matches()) {
            LOGGER.warn("Work start time has invalid format: {}", start);
            throw new SettingsValidationException("Work start time must be in HH:mm format.");
        }
        if (!TIME_PATTERN.matcher(end).matches()) {
            LOGGER.warn("Work end time has invalid format: {}", end);
            throw new SettingsValidationException("Work end time must be in HH:mm format.");
        }
    }
}
