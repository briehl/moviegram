package net.digitaldissonance.moviegram;

import java.io.File;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_imgcodecs;

public class App {
    private static final String MOVIE_FILE = "Babylon_5_01_-_01_Midnight_on_the_Firing_Line.avi";
    private static final int MAX_SECOND = 50;
    public static void main(String[] args) throws Exception {
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(new File(MOVIE_FILE));
        grabber.start();
        int numFrames = grabber.getLengthInFrames();
        OpenCVFrameConverter<Mat> converter = new OpenCVFrameConverter.ToMat();
        for (int i=0; i<100; i++) {
            Frame frame = grabber.grab();
            Mat source = converter.convert(frame);
            System.out.print(i + "/" + numFrames + ": ");
            if (source == null) {
                System.out.println("null frame");
            }
            else {
                System.out.println(source.address() + " " + source.size());
                opencv_imgcodecs.imwrite("out.jpg", source);
            }
        }
    }
}
