package com.eyeguard.util;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

/**
 * A custom modern ToggleSwitch control implemented on top of a styled CheckBox.
 * Encapsulates the CheckBox selection property and exposes it for FXML integration.
 */
public class ToggleSwitch extends HBox {

    private final CheckBox checkBox;

    /**
     * Constructs a new ToggleSwitch with selected state initialized to false.
     */
    public ToggleSwitch() {
        this(false);
    }

    /**
     * Constructs a new ToggleSwitch with the specified initial selected state.
     *
     * @param initialValue the initial selected state
     */
    public ToggleSwitch(final boolean initialValue) {
        this.checkBox = new CheckBox();
        this.checkBox.setSelected(initialValue);
        this.checkBox.getStyleClass().add("toggle-switch-checkbox");

        this.getChildren().add(this.checkBox);
        this.getStyleClass().add("toggle-switch");
    }

    /**
     * Gets the selected state property of the toggle switch.
     *
     * @return the selected state boolean property
     */
    public BooleanProperty selectedProperty() {
        return checkBox.selectedProperty();
    }

    /**
     * Checks if the toggle switch is currently selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return checkBox.isSelected();
    }

    /**
     * Sets the selected state of the toggle switch.
     *
     * @param value true to select, false to deselect
     */
    public void setSelected(final boolean value) {
        checkBox.setSelected(value);
    }
}
