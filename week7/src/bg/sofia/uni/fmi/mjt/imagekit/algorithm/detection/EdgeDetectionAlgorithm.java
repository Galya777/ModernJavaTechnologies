package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;



/**
 * Marker interface for edge detection algorithms.
 */

import modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;

/**
 * Represents an algorithm for edge detection in images.
 * Extends the basic ImageAlgorithm to provide edge detection capabilities.
 */
public interface EdgeDetectionAlgorithm extends ImageAlgorithm {

    /**
     * Applies edge detection to the given image and returns the result.
     * The input image is expected to be in grayscale format for optimal results.
     *
     * @param image the input image to process
     * @return a new BufferedImage with the detected edges
     * @throws IllegalArgumentException if the input image is null
     */
    @Override
    BufferedImage process(BufferedImage image);
}
