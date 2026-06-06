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
        final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();

        try {
            // Enable antialiasing for high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Clear background
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, size, size);
            g2d.setComposite(AlphaComposite.SrcOver);

            // Draw filled circle
            g2d.setColor(Color.decode(ICON_COLOR_HEX));
            final int diameter = size - (CIRCLE_OFFSET * 2);
            g2d.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, diameter, diameter);

            // Draw bold white letter "E" centered
            g2d.setColor(Color.WHITE);
            final int fontSize = (int) (size * FONT_SIZE_FACTOR);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));

            // Calculate centering coordinates using font metrics
            final FontMetrics metrics = g2d.getFontMetrics();
            final int x = (size - metrics.stringWidth(ICON_LETTER)) / 2;
            final int y = ((size - metrics.getHeight()) / 2) + metrics.getAscent();
            g2d.drawString(ICON_LETTER, x, y);
        } finally {
            g2d.dispose();
        }

        return image;
    }
}
