package net.digitaldissonance.moviegram;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;


public class ImageUtil {
    public static BufferedImage pixelsToImage(List<int[]> pixels, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = img.getRaster();
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                raster.setPixel(i, j, (int[])pixels.get(i * (height) + j));
            }
        }
        return img;
    }

    public static void convertAndSaveImage(List<int[]> avgPixels, int numImageFrames, int size, Path outputImage) throws IOException {
        System.out.println("Num pixels: " + avgPixels.size());
        System.out.println("Width (Num frames calculated): " + numImageFrames);
        System.out.println("Height: " + (avgPixels.size() / numImageFrames));

        BufferedImage image = ImageUtil.pixelsToImage(avgPixels, numImageFrames, avgPixels.size() / numImageFrames);
        File outputFile = new File(outputImage.toString());
        ImageIO.write(image, "png", outputFile);
    }
}