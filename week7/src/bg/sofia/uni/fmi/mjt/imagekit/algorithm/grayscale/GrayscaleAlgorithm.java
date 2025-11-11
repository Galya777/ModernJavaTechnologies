package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import java.awt.image.BufferedImage;

/**
 * Represents an algorithm for converting images to grayscale.
 * Extends the basic ImageAlgorithm to provide grayscale conversion capabilities.
 * Implementations should provide a method to convert a color image to grayscale
 * using a specific algorithm (e.g., luminosity, average, or desaturation).
 */
public interface GrayscaleAlgorithm extends ImageAlgorithm {

    /**
     * Converts the given color image to grayscale.
     * The implementation should handle the conversion according to the specific algorithm.
     *
     * @param image the input color image to be converted to grayscale
     * @return a new BufferedImage in grayscale (TYPE_INT_RGB)
     * @throws IllegalArgumentException if the input image is null
     */
    @Override
    BufferedImage process(BufferedImage image);
}
