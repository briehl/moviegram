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
    private int totalFrames = 0;
    private int currentFrame = 0;
    private int sampleRate = 1;
    private int nextFrameCounter = 0;
    private int usedFrames = 0;
    private AvgType avgType;
    private FrameType frameType;

    public enum AvgType {
        LINEAR, SQUARES
    }

    public enum FrameType {
        ALL, KEYFRAME
    }

    public Moviegram(AvgType avgType, FrameType frameType, int sampleRate) {
        this.avgType = avgType;
        this.frameType = frameType;
        this.sampleRate = sampleRate;
    }

    public Moviegram(AvgType avgType, FrameType frameType) {
        this(avgType, frameType, 1);
    }

    public boolean useThisFrame(Frame frame) {
        if (frame == null) {
            return false;
        }
        switch(frameType) {
            case KEYFRAME:
                if (!frame.keyFrame) {
                    return false;
                }
            case ALL:
            default:
                boolean isVideo = frame.getTypes().contains(Frame.Type.VIDEO);
                if (isVideo) {
                    nextFrameCounter--;
                    if (nextFrameCounter <= 0) {
                        nextFrameCounter = sampleRate;
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                return false;
        }
    }

    /**
     * Generates the Moviegram. Takes in a path to the movie file, opens it, strips out the frames
     * and averages their pixel color values, then squashes them together in a single image.
     * @param movieFile - the path to the file to moviegram-ify
     * @param outputImage - the path to the output moviegram image
     * @param avgType - how to calculate the "average" pixel color across rows
     * @param frameType - what frames to use
     * @throws IOException
     */
    public void build(Path movieFile, Path outputImage) throws IOException {
        nextFrameCounter = 0;
        int numImageFrames = 0;
        OpenCVFrameConverter<Mat> converter = new OpenCVFrameConverter.ToMat();
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(new File(movieFile.toString()));
        grabber.start();
        totalFrames = grabber.getLengthInFrames();

        List<int[]> avgPixels = new ArrayList<int[]>();

        /**
         * Iterate over all the frames. For each, follow the rules!
         * 1. If we're in keyframe mode, just grab the next keyframe.
         * 2. If not, just grab the next frame.
         *
         * Either way, convert that into a Mat. If it's null, then it's just an audio frame, or something else.
         */
        for (currentFrame = 0; currentFrame < totalFrames; currentFrame++) {
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

            if (!useThisFrame(frame)) {
                continue;
            }

            Mat source = converter.convert(frame);
            UByteIndexer index = source.createIndexer();
            int numCols = source.cols();
            int numRows = source.rows();

            System.out.println(currentFrame + "/" + totalFrames);
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

        ImageUtil.convertAndSaveImage(avgPixels, numImageFrames, avgPixels.size(), outputImage);
    }
}