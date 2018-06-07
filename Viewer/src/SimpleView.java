import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SimpleView implements View {
    private String fxml;

    private Parent root;
    private StargazerController controller;

    public SimpleView(String fxml) {
       this.fxml = fxml;
    }

    public void load() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(this.fxml));
        root = loader.load();
        controller = loader.getController();
    }

    public Parent getRoot() throws Exception {
        if (root == null)
            load();

        return root;
    }

    public StargazerController getController() {
        return controller;
    }

}