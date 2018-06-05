import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewRunner extends Application {
    public static String viewTemplate = "stargazer.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        View defaultView = new SimpleView(viewTemplate);
        Parent root = defaultView.getRoot();
        
        Scene scene = new Scene(root, 1024, 768);

        stage.setTitle("Star Gazer");
        stage.setScene(scene);
        stage.show();
    }

    /**
    * The main method is only needed for the IDE with limited
    * JavaFX support. Not needed for running from the command line.
    */
    public static void main(String[] args) {
        launch(args);
    }
}