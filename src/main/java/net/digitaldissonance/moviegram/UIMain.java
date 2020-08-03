package net.digitaldissonance.moviegram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.digitaldissonance.moviegram.Moviegram.AvgType;
import net.digitaldissonance.moviegram.Moviegram.FrameType;


public class UIMain extends Application {
	private File inputFile;
	private File outputImageFile;
	
    @Override
    public void start(Stage primaryStage) throws Exception {
    	final Button chooseInputFileBtn = new Button();
    	final FileChooser inputFileChooser = new FileChooser();
    	final Label fileLabel = new Label("no file selected");
    	chooseInputFileBtn.setText("Select Video File");
        chooseInputFileBtn.setOnAction(e -> {
        	File chosenFile = inputFileChooser.showOpenDialog(primaryStage);
        	if (chosenFile != null) {
        		this.inputFile = chosenFile;
	        	fileLabel.setText(this.inputFile.getAbsolutePath());
	        	inputFileChooser.setInitialDirectory(this.inputFile.getParentFile());
        	}
        });
        
        final Button chooseOutputFileBtn = new Button();
        final FileChooser outputFileChooser = new FileChooser();
        final Label outputFileLabel = new Label("no output image selected");
        chooseOutputFileBtn.setText("Select Output File");
        chooseOutputFileBtn.setOnAction(e -> {
        	File chosenFile = outputFileChooser.showSaveDialog(null);
        	if (chosenFile != null) {
        		this.outputImageFile = chosenFile;
        		outputFileLabel.setText(this.outputImageFile.getAbsolutePath());
        		outputFileChooser.setInitialDirectory(this.outputImageFile.getParentFile());
        	}
        });
        
        final Button runBtn = new Button();
        runBtn.setText("Run");
        runBtn.setOnAction(e -> {
        	System.out.println(this.inputFile.getAbsolutePath());
        	Moviegram moviegrammer = new Moviegram(AvgType.LINEAR, FrameType.ALL, 2);
        	try {
            	moviegrammer.build(Paths.get(inputFile.getAbsolutePath()), Paths.get(outputImageFile.getAbsolutePath()));        		
        	}
        	catch(IOException ioerr) {
        		System.out.println(ioerr);
        	}
        });
        
        VBox root = new VBox();
        root.getChildren().add(chooseInputFileBtn);
        root.getChildren().add(fileLabel);
        root.getChildren().add(chooseOutputFileBtn);
        root.getChildren().add(outputFileLabel);
        root.getChildren().add(runBtn);

        Scene scene = new Scene(root, 600, 250);

        primaryStage.setTitle("Moviegram");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}