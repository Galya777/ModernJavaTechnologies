package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;


import modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;

import java.awt.image.BufferedImage;

/**
 * Implements the Sobel edge detection algorithm.
 */
public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private static final int[][] SOBEL_X = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    private static final int[][] SOBEL_Y = {
        {-1, -2, -1},
        { 0,  0,  0},
        { 1,  2,  1}
    };

    private final GrayscaleAlgorithm grayscaleAlgorithm;
    private final int threshold;

    /**
     * Creates a SobelEdgeDetection instance with default threshold of 0.
     * @param grayscaleAlgorithm The grayscale algorithm to use for initial conversion
     */
    public SobelEdgeDetection(GrayscaleAlgorithm grayscaleAlgorithm) {
        this(grayscaleAlgorithm, 0);
    }

    /**
     * Creates a SobelEdgeDetection instance with a custom threshold.
     * @param grayscaleAlgorithm The grayscale algorithm to use for initial conversion
     * @param threshold The threshold for edge detection (0-255). Higher values result in fewer edges.
     */
    public SobelEdgeDetection(GrayscaleAlgorithm grayscaleAlgorithm, int threshold) {
        if (grayscaleAlgorithm == null) {
            throw new IllegalArgumentException("Grayscale algorithm cannot be null");
        }
        if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException("Threshold must be between 0 and 255");
        }
        this.grayscaleAlgorithm = grayscaleAlgorithm;
        this.threshold = threshold;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        // First convert to grayscale
        BufferedImage grayscale = grayscaleAlgorithm.process(image);
        int width = grayscale.getWidth();
        int height = grayscale.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Apply Sobel operator
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;
                
                // Apply Sobel kernels
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = (grayscale.getRGB(x + j, y + i) & 0xFF);
                        gx += pixel * SOBEL_X[i + 1][j + 1];
                        gy += pixel * SOBEL_Y[i + 1][j + 1];
                    }
                }
                
                // Calculate gradient magnitude
                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                
                // Apply threshold
                int edge = (magnitude > threshold) ? 255 : 0;
                
                // Invert to get black edges on white background
                edge = 255 - edge;
                int rgb = (edge << 16) | (edge << 8) | edge;
                result.setRGB(x, y, rgb);
            }
        }
        
        return result;
    }
}
