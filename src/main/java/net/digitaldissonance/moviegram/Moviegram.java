package net.digitaldissonance.moviegram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

public class Moviegram {
    public enum AvgType {
        LINEAR, SQUARES
    }

    public enum FrameType {
        ALL, KEYFRAME
    }

    public static void build(Path movieFile, Path outputImage, Moviegram.AvgType avgType, Moviegram.FrameType frameType) throws IOException {
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(new File(movieFile.toString()));
        grabber.start();
        int numFrames = grabber.getLengthInFrames();
        int numImageFrames = 0;
        OpenCVFrameConverter<Mat> converter = new OpenCVFrameConverter.ToMat();

        List<int[]> avgPixels = new ArrayList<int[]>();

        for (int i = 0; i < numFrames; i++) {
            Frame frame;
            switch (frameType) {
                case KEYFRAME:
                    frame = grabber.grabKeyFrame();
                    break;
                case ALL:
                default:
                    frame = grabber.grabFrame();
                    break;
            }
            Mat source = converter.convert(frame);
            System.out.print(i + "/" + numFrames + ": ");
            if (source == null) {
                System.out.println("null frame");
                if (frameType == FrameType.KEYFRAME) {
                    break;
                }
            } else {
                System.out.println(source.address() + " " + source.size());
                UByteIndexer index = source.createIndexer();
                int numCols = source.cols();
                int numRows = source.rows();
                int[][] avgCol = new int[numRows][3];
                for (int chan = 0; chan < 3; chan++) {
                    for (int row = 0; row < numRows; row++) {
                        avgCol[row][chan] = 0;
                    }
                }
                for (int row = 0; row < index.rows(); row++) {
                    for (int col = 0; col < index.cols(); col++) {
                        for (int chan = 0; chan < 3; chan++) {
                            double v = index.getDouble(row, col, chan);
                            switch (avgType) {
                                case SQUARES:
                                    avgCol[row][chan] += v * v;
                                    break;
                                case LINEAR:
                                default:
                                    avgCol[row][chan] += v;
                                    break;
                            }
                        }
                    }
                }
                for (int row = 0; row < numRows; row++) {
                    for (int chan = 0; chan < 3; chan++) {
                        switch (avgType) {
                            case SQUARES:
                                avgCol[row][chan] = (int) Math.sqrt(avgCol[row][chan] / numCols);
                                break;
                            case LINEAR:
                            default:
                                avgCol[row][chan] /= numCols;
                                break;
                        }
                    }
                    avgPixels.add(avgCol[row]);
                }
                numImageFrames++;
            }
        }

        ImageUtil.convertAndSaveImage(avgPixels, numImageFrames, avgPixels.size(), outputImage);

    }
}