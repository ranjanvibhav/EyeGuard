package com.eyeguard.util;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IconConverter.
 */
class IconConverterTest {

    @Test
    void testConversion() throws Exception {
        final File input = new File("src/main/resources/images/eye.png");
        final File output = new File("target/test-icon.ico");
        IconConverter.main(new String[]{input.getAbsolutePath(), output.getAbsolutePath()});
        assertTrue(output.exists());
        assertTrue(output.length() > 0);
    }

    @Test
    void testMainInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> IconConverter.main(new String[]{}));
    }
}
