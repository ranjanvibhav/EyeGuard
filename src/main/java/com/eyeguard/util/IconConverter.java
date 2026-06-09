package com.eyeguard.util;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Build utility to convert PNG icons to Windows ICO format (PNG format).
 */
public final class IconConverter {

    private IconConverter() {
        // Prevent instantiation
    }

    /**
     * Entry point to convert PNG to ICO.
     *
     * @param args args[0] = input PNG path, args[1] = output ICO path
     * @throws Exception if conversion fails
     */
    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: IconConverter <inputPNG> <outputICO>");
        }
        final File input = new File(args[0]);
        final File output = new File(args[1]);
        createParentDirs(output);
        final BufferedImage scaled = scaleImage(ImageIO.read(input), 256);
        writeIcon(scaled, output);
    }

    private static void createParentDirs(final File file) {
        final File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static BufferedImage scaleImage(final BufferedImage original, final int size) {
        final BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = scaled.createGraphics();
        try {
            g2d.drawImage(original.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
        } finally {
            g2d.dispose();
        }
        return scaled;
    }

    private static void writeIcon(final BufferedImage img, final File output) throws Exception {
        final byte[] pngBytes = getPngBytes(img);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(output)) {
            fos.write(new byte[]{0, 0, 1, 0, 1, 0});
            fos.write(new byte[]{0, 0, 0, 0, 1, 0, 32, 0});
            final int size = pngBytes.length;
            fos.write(new byte[]{
                (byte) (size & 0xFF), (byte) ((size >> 8) & 0xFF),
                (byte) ((size >> 16) & 0xFF), (byte) ((size >> 24) & 0xFF)
            });
            fos.write(new byte[]{22, 0, 0, 0});
            fos.write(pngBytes);
        }
        System.out.println("Icon created: " + output.getAbsolutePath());
    }

    private static byte[] getPngBytes(final BufferedImage img) throws Exception {
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        }
    }
}
