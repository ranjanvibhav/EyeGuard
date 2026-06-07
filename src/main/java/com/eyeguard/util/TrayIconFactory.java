package com.eyeguard.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Utility factory for creating programmatic system tray icon images.
 * Follows SOLID principles as a stateless, non-instantiable graphics helper.
 */
public final class TrayIconFactory {

    private static final double FONT_SIZE_FACTOR = 0.5;
    private static final int CIRCLE_OFFSET = 2;
    private static final String ICON_COLOR_HEX = "#2EC4B6";
    private static final String ICON_LETTER = "E";

    private TrayIconFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a buffered image of the specified size containing the EyeGuard icon (teal circle with white bold letter 'E').
     *
     * @param size the width and height of the square image
     * @return the generated BufferedImage containing the tray icon
     */
    public static BufferedImage createTrayIconImage(final int size) {
        return createTrayIconImage(size, Color.decode(ICON_COLOR_HEX));
    }

    /**
     * Creates a buffered image of the specified size with a custom circle fill color.
     *
     * @param size      width and height of the square image
     * @param fillColor fill color for the inner circle
     * @return the generated BufferedImage containing the tray icon
     */
    public static BufferedImage createTrayIconImage(final int size, final Color fillColor) {
        final BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(fillColor);
            final int d = size - (CIRCLE_OFFSET * 2);
            g2d.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, d, d);
            drawTextCentered(g2d, size);
        } finally {
            g2d.dispose();
        }
        return img;
    }

    private static void drawTextCentered(final Graphics2D g2d, final int size) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (size * FONT_SIZE_FACTOR)));
        final FontMetrics metrics = g2d.getFontMetrics();
        final int x = (size - metrics.stringWidth(ICON_LETTER)) / 2;
        final int y = ((size - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(ICON_LETTER, x, y);
    }
}
