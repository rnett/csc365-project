import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SimpleView implements View {
    private String fxml;
    
    public SimpleView(String fxml) {
       this.fxml = fxml;
    }

    public Parent getRoot() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(this.fxml));
        Parent root = loader.load();

        return root;
    }
}