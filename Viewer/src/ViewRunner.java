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
        QueriesWithDBConnection.connect(args[0], args[1], args[2]);
        launch(args);
        QueriesWithDBConnection.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        SimpleView defaultView = new SimpleView(viewTemplate);
        Parent root = defaultView.getRoot();

        Scene scene = new Scene(root);

        defaultView.getController().setStage(stage);

        stage.setTitle("Star Gazer");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}