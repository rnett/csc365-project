import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewRunner extends Application {
    public static String viewTemplate = "stargazer.fxml";

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        QueriesWithDBConnection.connect(args);
        launch(args);
        QueriesWithDBConnection.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        SimpleView defaultView = new SimpleView(viewTemplate);
        Parent root = defaultView.getRoot();

        Scene scene = new Scene(root, 1024, 768);

        defaultView.getController().setStage(stage);

        stage.setTitle("Star Gazer");
        stage.setScene(scene);
        stage.show();
    }
}