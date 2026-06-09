package com.eyeguard.service;

import com.eyeguard.exception.SettingsLoadException;
import com.eyeguard.exception.SettingsSaveException;
import com.eyeguard.exception.SettingsValidationException;
import com.eyeguard.model.Settings;

/**
 * Service interface for loading, saving, and validating application settings.
 */
public interface ConfigurationService {

    /**
     * Loads the settings configuration from disk.
     * If the configuration file does not exist, returns a new Settings instance with default values.
     *
     * @return the loaded or default Settings object (never null)
     * @throws SettingsLoadException if the file is corrupted or cannot be read
     */
    Settings loadSettings() throws SettingsLoadException;

    /**
     * Validates and saves the settings configuration to disk as JSON.
     *
     * @param settings the settings instance to save
     * @throws SettingsSaveException if writing or serializing settings fails
     * @throws SettingsValidationException if any setting constraint is violated
     */
    void saveSettings(Settings settings) throws SettingsSaveException, SettingsValidationException;

    /**
     * Returns a new Settings instance pre-populated with all default configuration values.
     *
     * @return a default Settings object
     */
    Settings getDefaultSettings();

    /**
     * Validates settings fields against validation constraints.
     *
     * @param settings the settings instance to validate
     * @throws SettingsValidationException if any value falls outside allowed limits or is malformed
     */
    void validateSettings(Settings settings) throws SettingsValidationException;
}
