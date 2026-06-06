package com.eyeguard.util;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for the {@link CountdownRingDrawer} utility class.
 * Verifies that the canvas drawing commands complete without raising exceptions.
 */
class CountdownRingDrawerTest {

    private static final double CANVAS_SIZE = 200.0;
    private static final double PROGRESS_EMPTY = 0.0;
    private static final double PROGRESS_HALF = 0.5;
    private static final double PROGRESS_FULL = 1.0;

    /**
     * Initializes the JavaFX Toolkit runtime required for Canvas rendering tests.
     */
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Toolkit already initialized, safe to ignore
        }
        Platform.setImplicitExit(false);
    }

    /**
     * Verifies that drawing with 0.0 progress does not throw exceptions.
     */
    @Test
    void testDrawRingWithZeroProgress() {
        final Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        assertDoesNotThrow(() ->
            CountdownRingDrawer.drawRing(canvas, PROGRESS_EMPTY, Color.BLACK, Color.RED)
        );
    }

    /**
     * Verifies that drawing with 0.5 progress does not throw exceptions.
     */
    @Test
    void testDrawRingWithHalfProgress() {
        final Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        assertDoesNotThrow(() ->
            CountdownRingDrawer.drawRing(canvas, PROGRESS_HALF, Color.BLACK, Color.RED)
        );
    }

    /**
     * Verifies that drawing with 1.0 progress does not throw exceptions.
     */
    @Test
    void testDrawRingWithFullProgress() {
        final Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        assertDoesNotThrow(() ->
            CountdownRingDrawer.drawRing(canvas, PROGRESS_FULL, Color.BLACK, Color.RED)
        );
    }

    /**
     * Verifies that drawing with a null Canvas reference handles the case safely without throwing.
     */
    @Test
    void testDrawRingWithNullCanvas() {
        assertDoesNotThrow(() ->
            CountdownRingDrawer.drawRing(null, PROGRESS_FULL, Color.BLACK, Color.RED)
        );
    }
}
