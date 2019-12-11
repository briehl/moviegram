package net.digitaldissonance.moviegram;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.digitaldissonance.moviegram.Moviegram.AvgType;
import net.digitaldissonance.moviegram.Moviegram.FrameType;

public class App {
    public static void main(String[] args) throws Exception {
        // Path movieFile = Paths.get("F:\\Video\\Movies\\Heavy Metal 1981 720p BluRay x264 CiNEFiLE\\Heavy.Metal.1981.720p.BluRay.x264-CiNEFiLE.mkv");
        // Path outputFile = Paths.get("F:\\Video\\Movies\\Heavy Metal 1981 720p BluRay x264 CiNEFiLE\\Heavy.Metal_linear_all.png");
        Path movieFile = Paths.get("F:\\Video\\Animation\\Looney Tunes\\Looney Tunes - Golden Collection, Vol. 1\\Disc 1\\14. Rabbit of Seville (1950).avi");
        Path outputFile = Paths.get("C:\\Users\\brieh\\Desktop\\Rabbit of Seville (1950) - 2.png");
        Moviegram moviegrammer = new Moviegram(AvgType.LINEAR, FrameType.ALL, 2);
        moviegrammer.build(movieFile, outputFile);
    }
}
