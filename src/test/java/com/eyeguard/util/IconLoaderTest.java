package com.eyeguard.util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IconLoader.
 */
class IconLoaderTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Already initialized
        }
    }

    @Test
    void testLoadJavaFXImage() {
        final Image img = IconLoader.loadJavaFXImage();
        assertNotNull(img);
    }

    @Test
    void testLoadJavaFXImageScaled64() {
        final Image img = IconLoader.loadJavaFXImage(64, 64);
        assertNotNull(img);
        assertEquals(64.0, img.getWidth());
        assertEquals(64.0, img.getHeight());
    }

    @Test
    void testLoadJavaFXImageScaled32() {
        final Image img = IconLoader.loadJavaFXImage(32, 32);
        assertNotNull(img);
    }

    @Test
    void testLoadAwtImage() {
        final java.awt.Image img64 = IconLoader.loadAwtImage(64);
        assertNotNull(img64);
        final java.awt.Image img16 = IconLoader.loadAwtImage(16);
        assertNotNull(img16);
    }
}
