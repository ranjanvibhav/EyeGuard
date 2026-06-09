package com.eyeguard.service;

import com.eyeguard.exception.SettingsLoadException;
import com.eyeguard.exception.SettingsSaveException;
import com.eyeguard.exception.SettingsValidationException;
import com.eyeguard.model.Settings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ConfigurationServiceImpl} verifying loading, saving, and validation rules.
 */
class ConfigurationServiceImplTest {

    @TempDir
    Path tempDir;

    private ConfigurationService getService(final String filename) {
        return new ConfigurationServiceImpl(tempDir.resolve(filename));
    }

    @Test
    void testLoadSettingsWhenFileDoesNotExist() throws Exception {
        final Path path = tempDir.resolve("nonexistent.json");
        final ConfigurationService service = new ConfigurationServiceImpl(path);
        final Settings settings = service.loadSettings();
        
        assertNotNull(settings);
        assertEquals(20, settings.getReminderIntervalMinutes());
        assertFalse(Files.exists(path));
    }

    @Test
    void testSaveSettingsCreatesFileAndDirectories() throws Exception {
        final Path nestedPath = tempDir.resolve("subdir").resolve("nested-settings.json");
        final ConfigurationService service = new ConfigurationServiceImpl(nestedPath);
        final Settings settings = new Settings();
        
        service.saveSettings(settings);
        assertTrue(Files.exists(nestedPath));
        
        final String content = Files.readString(nestedPath);
        assertTrue(content.contains("reminderIntervalMinutes"));
    }

    @Test
    void testSaveAndLoadEquality() throws Exception {
        final ConfigurationService service = getService("save-load.json");
        final Settings original = new Settings(15, 30, 10, false, "10:00", "18:00", true, false, false, false, true);
        
        service.saveSettings(original);
        final Settings loaded = service.loadSettings();
        
        assertEquals(original, loaded);
        assertEquals(15, loaded.getReminderIntervalMinutes());
        assertEquals("10:00", loaded.getWorkStartTime());
    }

    @Test
    void testLoadCorruptedJsonThrowsException() throws Exception {
        final Path path = tempDir.resolve("corrupted.json");
        Files.writeString(path, "{invalid_json:");
        final ConfigurationService service = new ConfigurationServiceImpl(path);
        
        assertThrows(SettingsLoadException.class, service::loadSettings);
    }

    @Test
    void testValidateSettingsIntervalConstraints() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        settings.setReminderIntervalMinutes(4);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setReminderIntervalMinutes(61);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
    }

    @Test
    void testValidateSettingsDurationConstraints() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        settings.setBreakDurationSeconds(9);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setBreakDurationSeconds(61);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
    }

    @Test
    void testValidateSettingsSnoozeConstraints() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        settings.setSnoozeDurationMinutes(4);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setSnoozeDurationMinutes(31);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
    }

    @Test
    void testValidateSettingsStartTimeNullOrBlank() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        settings.setWorkStartTime(null);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setWorkStartTime("   ");
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
    }

    @Test
    void testValidateSettingsEndTimeNullOrBlank() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        settings.setWorkEndTime(null);
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setWorkEndTime("   ");
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
    }

    @Test
    void testValidateSettingsTimeFormats() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        settings.setWorkStartTime("9:00");
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setWorkStartTime("0900");
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
        
        settings.setWorkStartTime("09:00");
        settings.setWorkEndTime("7:00 PM");
        assertThrows(SettingsValidationException.class, () -> service.validateSettings(settings));
    }

    @Test
    void testValidateValidSettings() {
        final ConfigurationService service = getService("validate.json");
        final Settings settings = new Settings();
        
        assertDoesNotThrow(() -> service.validateSettings(settings));
    }

    @Test
    void testGetDefaultSettings() {
        final ConfigurationService service = getService("default.json");
        final Settings settings = service.getDefaultSettings();
        
        assertNotNull(settings);
        assertEquals(20, settings.getReminderIntervalMinutes());
    }

    @Test
    void testSaveInvalidSettingsDoesNotCreateFile() {
        final Path path = tempDir.resolve("invalid_save.json");
        final ConfigurationService service = new ConfigurationServiceImpl(path);
        final Settings settings = new Settings();
        settings.setReminderIntervalMinutes(3);
        
        assertThrows(SettingsValidationException.class, () -> service.saveSettings(settings));
        assertFalse(Files.exists(path));
    }

    @Test
    void testCreateDefaultFactoryMethod() {
        final ConfigurationServiceImpl service = ConfigurationServiceImpl.createDefault();
        assertNotNull(service);
        assertNotNull(service.getDefaultSettings());
    }

    @Test
    void testSaveSettingsThrowsIOException() {
        // Pointing settings to a directory path will cause ObjectMapper to throw IOException on write
        final Path dirPath = tempDir.resolve("directory.json");
        assertDoesNotThrow(() -> Files.createDirectories(dirPath));
        final ConfigurationService service = new ConfigurationServiceImpl(dirPath);
        final Settings settings = new Settings();
        
        assertThrows(SettingsSaveException.class, () -> service.saveSettings(settings));
    }
}
