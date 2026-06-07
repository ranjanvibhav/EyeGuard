/**
 * Module definition for the EyeGuard application.
 */
module com.eyeguard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires com.sun.jna;
    requires com.sun.jna.platform;

    opens com.eyeguard.app to javafx.graphics;
    opens com.eyeguard.view to javafx.fxml;
    exports com.eyeguard.model;
    opens com.eyeguard.model to com.fasterxml.jackson.databind;
}
