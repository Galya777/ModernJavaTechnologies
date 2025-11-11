package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;

import java.awt.image.BufferedImage;

/**
 * Converts an image to grayscale using the desaturation method.
 * The formula used is: (max(R, G, B) + min(R, G, B)) / 2
 */
public class DesaturationGrayscale implements GrayscaleAlgorithm {

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
                
                // Calculate grayscale value using desaturation method
                int max = Math.max(r, Math.max(g, b));
                int min = Math.min(r, Math.min(g, b));
                int gray = (max + min) / 2;
                
                // Create grayscale RGB value
                int grayRgb = (gray << 16) | (gray << 8) | gray;
                grayscaleImage.setRGB(x, y, grayRgb);
            }
        }
        
        return grayscaleImage;
    }
}
