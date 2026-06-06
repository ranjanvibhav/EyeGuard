package com.eyeguard.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;

/**
 * Utility class to draw the countdown ring on a JavaFX Canvas.
 * Follows SOLID principles as a stateless, non-instantiable drawing helper.
 */
public final class CountdownRingDrawer {

    private static final double STROKE_WIDTH = 12.0;
    private static final double PADDING = 24.0;
    private static final double START_ANGLE = 90.0;
    private static final double FULL_DEGREES = 360.0;

    private CountdownRingDrawer() {
        // Prevent instantiation of utility class
    }

    /**
     * Draws the circular countdown ring on the provided canvas.
     *
     * @param canvas the Canvas to draw on
     * @param progress the progress ratio from 0.0 (empty) to 1.0 (full)
     * @param trackColor the color of the background track circle
     * @param progressColor the color of the active progress arc
     */
    public static void drawRing(final Canvas canvas, final double progress,
                                final Color trackColor, final Color progressColor) {
        if (canvas == null) {
            return;
        }

        final double width = canvas.getWidth();
        final double height = canvas.getHeight();
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Clear the canvas
        gc.clearRect(0, 0, width, height);

        final double diameter = Math.min(width, height) - PADDING;
        final double x = (width - diameter) / 2.0;
        final double y = (height - diameter) / 2.0;

        // Set stroke parameters
        gc.setLineWidth(STROKE_WIDTH);
        gc.setLineCap(StrokeLineCap.ROUND);

        // 2. Draw full circle as track
        gc.setStroke(trackColor);
        gc.strokeOval(x, y, diameter, diameter);

        // 3. Draw arc from top (90 degrees) clockwise (negative extent)
        if (progress > 0.0) {
            gc.setStroke(progressColor);
            final double extentAngle = -progress * FULL_DEGREES;
            gc.strokeArc(x, y, diameter, diameter, START_ANGLE, extentAngle, ArcType.OPEN);
        }
    }
}
