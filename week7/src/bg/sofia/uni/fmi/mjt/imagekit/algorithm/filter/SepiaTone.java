package bg.sofia.uni.fmi.mjt.imagekit.algorithm.filter;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import java.awt.image.BufferedImage;

/**
 * Applies a sepia tone filter to an image.
 * This gives the image a warm brownish tone, similar to old photographs.
 */
public class SepiaTone implements ImageAlgorithm {

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                
                // Extract RGB components
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Calculate sepia values
                int newR = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int newG = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int newB = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                
                // Clamp values to 0-255
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                
                // Set the new pixel value
                int newRgb = (newR << 16) | (newG << 8) | newB;
                result.setRGB(x, y, newRgb);
            }
        }
        
        return result;
    }
}
