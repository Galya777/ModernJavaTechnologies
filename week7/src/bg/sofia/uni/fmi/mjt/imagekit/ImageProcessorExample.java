package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Example usage of the image processing library.
 */
public class ImageProcessorExample {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ImageProcessorExample <input-image-path> [output-dir]");
            return;
        }
        
        String inputPath = args[0];
        String outputDir = args.length > 1 ? args[1] : ".";
        
        FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();
        
        try {
            // Load the input image
            BufferedImage image = fsImageManager.loadImage(new File(inputPath));
            System.out.println("Successfully loaded image: " + inputPath);
            
            // Create output directory if it doesn't exist
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
            
            // Process with grayscale
            LuminosityGrayscale grayscaleAlgorithm = new LuminosityGrayscale();
            BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);
            
            File grayscaleOutput = new File(outputDir, "grayscale_" + new File(inputPath).getName());
            fsImageManager.saveImage(grayscaleImage, grayscaleOutput);
            System.out.println("Saved grayscale image to: " + grayscaleOutput.getAbsolutePath());
            
            // Process with edge detection
            SobelEdgeDetection edgeDetection = new SobelEdgeDetection((GrayscaleAlgorithm) grayscaleAlgorithm);
            BufferedImage edgeImage = edgeDetection.process(image);
            
            File edgeOutput = new File(outputDir, "edges_" + new File(inputPath).getName());
            fsImageManager.saveImage(edgeImage, edgeOutput);
            System.out.println("Saved edge-detected image to: " + edgeOutput.getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
