/**
 * Wombat Text Editor Main Class
 * 
 * @author Namchee
 * @version 1.0
 */

package app;

import java.io.IOException;

import app.view.AboutController;
import app.view.OverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class App extends Application {
    
    private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image("list.png"));
		this.primaryStage.setResizable(false);
		
		this.showPane();
	}
	
	/**
	 * showPane() method
	 * Load the main window
	 * 
	 * @throws IOException
	 */
	private void showPane() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/OverviewWindow.fxml"));
	        BorderPane pane = (BorderPane)loader.load();
	        
	        Scene scene = new Scene(pane);
	        this.primaryStage.setScene(scene);
	        
	        OverviewController controller = loader.getController();
	        controller.setReference(this);
	        this.primaryStage.setOnCloseRequest(e -> {
	            controller.close();
	        });

	        this.primaryStage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * showAboutWindow()
	 * Show about window when about is clicked
	 * 
	 * @throws IOException
	 */
	public void showAboutWindow() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/AboutWindow.fxml"));
	        AnchorPane pane = (AnchorPane)loader.load();
	        
	        Stage aboutStage = new Stage();
	        aboutStage.setTitle("Apa ini?");
	        aboutStage.initOwner(primaryStage);
	        aboutStage.initModality(Modality.WINDOW_MODAL);
	        aboutStage.setResizable(false);
	        
	        Scene aboutScene = new Scene(pane);
	        aboutStage.setScene(aboutScene);
	        
	        AboutController controller = loader.getController();
	        controller.setReference(this);
	        
	        aboutStage.showAndWait();
	    } catch (IOException e) {
	        System.out.println("KUALAT QAMU NAKS");
	        e.printStackTrace();
	    }
	}

	public static void main(String[] args) {
		launch(args);
	}
}
