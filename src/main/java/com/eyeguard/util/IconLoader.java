package com.eyeguard.util;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

/**
 * Utility class to load and scale application icons.
 */
public final class IconLoader {

    private static final Logger log = LoggerFactory.getLogger(IconLoader.class);
    private static final String ICON_PATH = "/images/eye.png";

    private IconLoader() {
        // Prevent instantiation
    }

    /**
     * Loads eye.png as a JavaFX Image.
     *
     * @return the loaded Image, or null if loading fails
     */
    public static Image loadJavaFXImage() {
        try {
            return new Image(Objects.requireNonNull(
                    IconLoader.class.getResource(ICON_PATH)).toExternalForm());
        } catch (final Exception e) {
            log.warn("Could not load eye.png icon", e);
            return null;
        }
    }

    /**
     * Loads eye.png as a JavaFX Image scaled to width x height.
     *
     * @param width  requested width
     * @param height requested height
     * @return the loaded Image, or null if loading fails
     */
    public static Image loadJavaFXImage(final int width, final int height) {
        try {
            return new Image(
                    Objects.requireNonNull(IconLoader.class.getResourceAsStream(ICON_PATH)),
                    width, height, true, true);
        } catch (final Exception e) {
            log.warn("Could not load eye.png icon scaled to {}x{}", width, height, e);
            return null;
        }
    }

    /**
     * Loads eye.png as a scaled AWT Image for tray icon.
     *
     * @param size width and height of the square image
     * @return the loaded java.awt.Image, or null if loading fails
     */
    public static java.awt.Image loadAwtImage(final int size) {
        try (final InputStream stream = IconLoader.class.getResourceAsStream(ICON_PATH)) {
            if (stream == null) {
                return null;
            }
            final BufferedImage original = ImageIO.read(stream);
            return original.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        } catch (final Exception e) {
            log.warn("Could not load eye.png AWT icon scaled to size {}", size, e);
            return null;
        }
    }
}
