package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

/**
 * Converts an image to grayscale using the luminosity method.
 * The formula used is: 0.21 * R + 0.72 * G + 0.07 * B
 */
public class LuminosityGrayscale implements GrayscaleAlgorithm {

    private static final double RED_COEFFICIENT = 0.21;
    private static final double GREEN_COEFFICIENT = 0.72;
    private static final double BLUE_COEFFICIENT = 0.07;

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                
                // Extract RGB components
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Calculate grayscale value using luminosity method
                int gray = (int) (r * RED_COEFFICIENT + g * GREEN_COEFFICIENT + b * BLUE_COEFFICIENT);
                
                // Ensure the value is within valid range (0-255)
                gray = Math.min(255, Math.max(0, gray));
                
                // Create grayscale RGB value
                int grayRgb = (gray << 16) | (gray << 8) | gray;
                grayscaleImage.setRGB(x, y, grayRgb);
            }
        }
        
        return grayscaleImage;
    }
}
