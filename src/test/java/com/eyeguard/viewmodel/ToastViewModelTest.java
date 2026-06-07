package com.eyeguard.viewmodel;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for ToastViewModel verifying property updates and initial state.
 */
class ToastViewModelTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Already initialized
        }
    }

    @Test
    void testInitialState() {
        final ToastViewModel viewModel = new ToastViewModel();
        assertEquals("", viewModel.getMessageText());
        assertEquals("", viewModel.getErrorMessage());
    }

    @Test
    void testPropertyChanges() {
        final ToastViewModel viewModel = new ToastViewModel();
        viewModel.messageTextProperty().set("Hello");
        viewModel.errorMessageProperty().set("Error");
        assertEquals("Hello", viewModel.getMessageText());
        assertEquals("Error", viewModel.getErrorMessage());
    }
}
