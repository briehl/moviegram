package net.digitaldissonance.moviegram;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception {
        Path movieFile = Paths.get("F:\\Video\\Movies\\Heavy Metal 1981 720p BluRay x264 CiNEFiLE\\Heavy.Metal.1981.720p.BluRay.x264-CiNEFiLE.mkv");
        Path outputFile = Paths.get("F:\\Video\\Movies\\Heavy Metal 1981 720p BluRay x264 CiNEFiLE\\Heavy.Metal_linear_all.png");
        Moviegram.build(movieFile, outputFile, Moviegram.AvgType.LINEAR, Moviegram.FrameType.ALL);
    }
}
