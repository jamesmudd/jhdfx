package io.jhdf.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Test extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("jHDfx");
		primaryStage.getIcons().setAll(new Image(getClass().getResourceAsStream("icons/hdf5.png")));
		Pane pane = FXMLLoader.load(getClass().getResource("test.fxml"));
		Scene scene = new Scene(pane);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
