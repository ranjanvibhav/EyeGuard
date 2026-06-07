package com.eyeguard.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for presentation state of the toast notifications.
 */
public class ToastViewModel {

    private final StringProperty messageText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    /**
     * Constructs the ToastViewModel.
     */
    public ToastViewModel() {
        // Initialization
    }

    public StringProperty messageTextProperty() {
        return messageText;
    }

    public String getMessageText() {
        return messageText.get();
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }
}
