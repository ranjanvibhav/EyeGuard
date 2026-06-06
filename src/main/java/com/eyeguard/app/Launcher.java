package com.eyeguard.app;

import javafx.application.Application;

/**
 * Launcher class to serve as the main entry point for the JVM.
 * This class is necessary to avoid classpath issues when launching JavaFX applications
 * without the JavaFX runtime environment module path configuration.
 */
public class Launcher {

    /**
     * The main entry point of the JVM. Launches the JavaFX Application.
     *
     * @param arguments the command line arguments passed to the application
     */
    public static void main(final String[] arguments) {
        Application.launch(EyeGuardApp.class, arguments);
    }
}
