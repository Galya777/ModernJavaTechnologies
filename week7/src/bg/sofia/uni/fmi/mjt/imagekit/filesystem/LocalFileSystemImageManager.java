package modernJava.week7.src.bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of FileSystemImageManager for local file system operations.
 */
public class LocalFileSystemImageManager implements bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager {

    private static final String[] SUPPORTED_EXTENSIONS = {"jpg", "jpeg", "png", "bmp", "gif", "tif", "tiff", "webp"};
    
    static {
        // Enable reading/writing of additional formats
        System.setProperty("java.awt.headless", "true");
        // Register additional image readers/writers
        String[] formats = {"tif", "tiff", "webp"};
        for (String format : formats) {
            ImageIO.scanForPlugins();
        }
    }

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("File does not exist or is not a regular file: " + imageFile.getPath());
        }

        String fileName = imageFile.getName().toLowerCase();
        boolean supported = false;
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith("." + ext)) {
                supported = true;
                break;
            }
        }

        if (!supported) {
            throw new IOException("Unsupported image format. Supported formats: " + 
                String.join(", ", SUPPORTED_EXTENSIONS));
        }

        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new IOException("Failed to read image: " + imageFile.getPath());
            }
            return image;
        } catch (IOException e) {
            throw new IOException("Error reading image file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Images directory cannot be null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("Directory does not exist or is not a directory: " + 
                imagesDirectory.getPath());
        }

        List<BufferedImage> images = new ArrayList<>();
        File[] files = imagesDirectory.listFiles();
        
        if (files == null) {
            throw new IOException("Error listing directory contents");
        }

        for (File file : files) {
            try {
                if (file.isFile()) {
                    for (String ext : SUPPORTED_EXTENSIONS) {
                        if (file.getName().toLowerCase().endsWith("." + ext)) {
                            images.add(loadImage(file));
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                // Skip files that can't be read
                continue;
            }
        }

        if (images.isEmpty()) {
            throw new IOException("No supported image files found in directory: " + 
                imagesDirectory.getPath());
        }

        return images;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null || imageFile == null) {
            throw new IllegalArgumentException("Image and image file cannot be null");
        }

        if (imageFile.exists()) {
            throw new IOException("File already exists: " + imageFile.getPath());
        }

        // Create parent directories if they don't exist
        File parent = imageFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        String fileName = imageFile.getName().toLowerCase();
        String formatName = "jpg"; // default format
        
        if (fileName.endsWith(".png")) {
            formatName = "png";
        } else if (fileName.endsWith(".bmp")) {
            formatName = "bmp";
        } else if (fileName.endsWith(".gif")) {
            formatName = "gif";
        } else if (fileName.endsWith(".tif") || fileName.endsWith(".tiff")) {
            formatName = "tiff";
        } else if (fileName.endsWith(".webp")) {
            formatName = "webp";
        }
        
        try {
            if (!ImageIO.write(image, formatName, imageFile)) {
                throw new IOException("No appropriate writer found for format: " + formatName);
            }
        } catch (IOException e) {
            throw new IOException("Error writing image file: " + e.getMessage(), e);
        }
    }
}
