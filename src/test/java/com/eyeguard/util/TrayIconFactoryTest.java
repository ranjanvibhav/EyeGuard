package com.eyeguard.util;

import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TrayIconFactory} to verify programmatic icon generation.
 */
class TrayIconFactoryTest {

    /**
     * Verifies that the created tray icon image has the correct dimensions and format.
     */
    @Test
    void testCreateTrayIconImage() {
        final int size = 32;
        final BufferedImage image = TrayIconFactory.createTrayIconImage(size);
        
        assertNotNull(image);
        assertEquals(size, image.getWidth());
        assertEquals(size, image.getHeight());
        assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
    }

    /**
     * Verifies that the created tray icon image can be rendered in multiple resolutions.
     */
    @Test
    void testCreateTrayIconImageDifferentSizes() {
        final int size16 = 16;
        final int size64 = 64;
        
        final BufferedImage image16 = TrayIconFactory.createTrayIconImage(size16);
        final BufferedImage image64 = TrayIconFactory.createTrayIconImage(size64);
        
        assertNotNull(image16);
        assertEquals(size16, image16.getWidth());
        
        assertNotNull(image64);
        assertEquals(size64, image64.getWidth());
    }

    /**
     * Verifies that calling the private constructor via reflection works and covers the lines.
     *
     * @throws Exception if reflection access fails
     */
    @Test
    void testConstructorIsPrivate() throws Exception {
        final Constructor<TrayIconFactory> constructor = TrayIconFactory.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        final TrayIconFactory instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
