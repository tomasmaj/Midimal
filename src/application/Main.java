package application;
	
import controller.ViewController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that starts the application and loads all views and the scene
 * 
 * @author Tomas Majling
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			// Instantiate the master controller
			ViewController vc = new ViewController();

			vc.loadView("menu", "../view/MenuLayout.fxml");
			vc.loadView("sequencer", "../view/SequencerLayout.fxml");

			vc.setTopView("menu");
			vc.setCenterView("sequencer");

			Group root = new Group();
			root.getChildren().addAll(vc);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(920);
			primaryStage.setMinHeight(720);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Midimal");
			primaryStage.show();


		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
