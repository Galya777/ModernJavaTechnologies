package bg.sofia.uni.fmi.mjt.imagekit.algorithm.filter;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Applies a Gaussian blur filter to an image.
 * This implementation uses a 5x5 Gaussian kernel with sigma = 1.0.
 */
public class GaussianBlur implements ImageAlgorithm {

    private static final float[] GAUSSIAN_KERNEL = {
        1/256f, 4/256f,  6/256f,  4/256f,  1/256f,
        4/256f, 16/256f, 24/256f, 16/256f, 4/256f,
        6/256f, 24/256f, 36/256f, 24/256f, 6/256f,
        4/256f, 16/256f, 24/256f, 16/256f, 4/256f,
        1/256f, 4/256f,  6/256f,  4/256f,  1/256f
    };

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        // Create kernel for convolution
        Kernel kernel = new Kernel(5, 5, GAUSSIAN_KERNEL);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        // Create destination image with the same dimensions and type
        BufferedImage result = new BufferedImage(
            image.getWidth(), 
            image.getHeight(), 
            image.getType()
        );
        
        // Apply the filter
        return op.filter(image, result);
    }
}
