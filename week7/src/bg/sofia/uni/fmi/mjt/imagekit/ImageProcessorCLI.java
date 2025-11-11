package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.filter.GaussianBlur;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.filter.SepiaTone;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.AverageGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.DesaturationGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Command-line interface for the Image Processor.
 * 
 * Usage:
 *   java ImageProcessorCLI <command> [options] <input-file> [output-file]
 * 
 * Commands:
 *   grayscale <method> - Convert to grayscale using specified method (luminosity|average|desaturation)
 *   edges [threshold] - Detect edges using Sobel operator (threshold 0-255, default: 0)
 *   blur - Apply Gaussian blur
 *   sepia - Apply sepia tone filter
 *   help - Show this help message
 */
public class ImageProcessorCLI {
    
    private static final String USAGE = "" +
        "Usage: java ImageProcessorCLI <command> [options] <input-file> [output-file]\n" +
        "\n" +
        "Commands:\n" +
        "  grayscale <method>    Convert to grayscale using specified method (luminosity|average|desaturation)\n" +
        "  edges [threshold]     Detect edges using Sobel operator (threshold 0-255, default: 0)\n" +
        "  blur                  Apply Gaussian blur\n" +
        "  sepia                 Apply sepia tone filter\n" +
        "  help                  Show this help message\n";
    
    private final FileSystemImageManager fsImageManager;
    
    public ImageProcessorCLI() {
        this.fsImageManager = new LocalFileSystemImageManager();
    }
    
    public static void main(String[] args) {
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            System.out.println(USAGE);
            return;
        }
        
        ImageProcessorCLI cli = new ImageProcessorCLI();
        
        try {
            cli.processCommand(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Use 'help' for usage information");
            System.exit(1);
        }
    }
    
    private void processCommand(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Insufficient arguments");
        }
        
        String command = args[0].toLowerCase();
        String inputPath = args[args.length - 1];
        String outputPath = getOutputPath(args, inputPath);
        
        // Load the input image
        BufferedImage image = fsImageManager.loadImage(new File(inputPath));
        BufferedImage result;
        
        switch (command) {
            case "grayscale":
                if (args.length < 3) {
                    throw new IllegalArgumentException("Grayscale method not specified");
                }
                result = applyGrayscale(image, args[1]);
                break;
                
            case "edges":
                int threshold = 0;
                if (args.length >= 3) {
                    try {
                        threshold = Integer.parseInt(args[1]);
                        if (threshold < 0 || threshold > 255) {
                            throw new IllegalArgumentException("Threshold must be between 0 and 255");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid threshold value: " + args[1]);
                    }
                }
                result = detectEdges(image, threshold);
                break;
                
            case "blur":
                result = applyGaussianBlur(image);
                break;
                
            case "sepia":
                result = applySepiaTone(image);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
        
        // Save the result
        fsImageManager.saveImage(result, new File(outputPath));
        System.out.println("Processed image saved to: " + outputPath);
    }
    
    private String getOutputPath(String[] args, String inputPath) {
        // If there are at least 3 arguments and the second to last is not a number (not a threshold)
        if (args.length >= 3 && !args[args.length - 2].matches("-?\\d+")) {
            return args[args.length - 1];
        }
        
        // Otherwise, generate output path from input path
        File inputFile = new File(inputPath);
        String fileName = inputFile.getName();
        String baseName = fileName.lastIndexOf('.') > 0 
            ? fileName.substring(0, fileName.lastIndexOf('.')) 
            : fileName;
        String extension = fileName.lastIndexOf('.') > 0 
            ? fileName.substring(fileName.lastIndexOf('.')) 
            : ".jpg";
            
        return baseName + "_processed" + extension;
    }
    
    private BufferedImage applyGrayscale(BufferedImage image, String method) {
        GrayscaleAlgorithm grayscaleAlgorithm;
        
        switch (method.toLowerCase()) {
            case "luminosity":
                grayscaleAlgorithm = new LuminosityGrayscale();
                break;
            case "average":
                grayscaleAlgorithm = new AverageGrayscale();
                break;
            case "desaturation":
                grayscaleAlgorithm = new DesaturationGrayscale();
                break;
            default:
                throw new IllegalArgumentException("Unknown grayscale method: " + method + 
                    ". Use 'luminosity', 'average', or 'desaturation'");
        }
        
        return grayscaleAlgorithm.process(image);
    }
    
    private BufferedImage detectEdges(BufferedImage image, int threshold) {
        GrayscaleAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        SobelEdgeDetection edgeDetection = new SobelEdgeDetection(grayscaleAlgorithm, threshold);
        return edgeDetection.process(image);
    }
    
    private BufferedImage applyGaussianBlur(BufferedImage image) {
        GaussianBlur blur = new GaussianBlur();
        return blur.process(image);
    }
    
    private BufferedImage applySepiaTone(BufferedImage image) {
        SepiaTone sepia = new SepiaTone();
        return sepia.process(image);
    }
}
