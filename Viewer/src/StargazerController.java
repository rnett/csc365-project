import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class StargazerController implements Initializable {
    @FXML
    private ListView<String> planetList;
    @FXML
    private TableView<SolarSystem> starTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn planetColumn;
    @FXML
    private TableColumn goldilocksColumn;

    @FXML
    private ComboBox typeSelect;
    @FXML
    private TextField minPlanets;
    @FXML
    private TextField maxPlanets;
    @FXML
    private TextField minGoldilocks;
    @FXML
    private TextField maxGoldilocks;
    @FXML
    private TextField minDistance;
    @FXML
    private TextField maxDistance;

    @FXML
    private Label starName;
    @FXML
    private TextField starType;
    @FXML
    private TextField starColor;
    @FXML
    private TextField starMass;
    @FXML
    private TextField starRadius;
    @FXML
    private TextField starTemp;
    @FXML
    private TextField starDistance;

    @FXML
    private TextField planetName;
    @FXML
    private CheckBox planetGoldilocks;
    @FXML
    private TextField planetMass;
    @FXML
    private TextField planetRadius;
    @FXML
    private TextField planetDensity;
    @FXML
    private TextField orbitRadius;
    @FXML
    private TextField orbitPeriod;

    @FXML
    private Button importButton;

    @FXML
    private AnchorPane starViewer;

    @FXML
    private Text starsErrorMessage;

    @FXML
    private Text importErrorMessage;

    private FileChooser fileChooser = null;
    private Stage stage;

    private SolarSystem solarSystem;
    private Star star;
    private Planet planet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileChooser = new FileChooser();

        nameColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, String>("name"));
        planetColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Integer>("planets"));
        goldilocksColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Integer>("goldilocks"));

        typeSelect.getItems().addAll(
           "All",
           "Unknown",
           "Bright Giant",
           "Giant",
           "Subgiant",
           "Main Sequence"
        );
        importButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                try {

                    fileChooser.setInitialDirectory(new File("."));
                    fileChooser.setTitle("Select Data File");

                    importErrorMessage.setFill(Color.CYAN);
                    importErrorMessage.setText("Importing...");

                    File f = fileChooser.showOpenDialog(stage);


                    Parser.parse(f.getAbsolutePath());

                    ArrayList<SolarSystem> stars = getSolarSystems();

                    starTable.setItems(FXCollections.observableArrayList(stars));
                    starList.refresh();
                    importErrorMessage.setFill(Color.GREEN);
                    importErrorMessage.setText("Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    importErrorMessage.setFill(Color.RED);
                    importErrorMessage.setText("Error");
                }

            }
        });

        ObservableList<SolarSystem> stars = FXCollections.observableArrayList(getSolarSystems());

        starTable.setItems(FXCollections.observableArrayList(stars));

        starTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SolarSystem>() {
            @Override
            public void changed(ObservableValue<? extends SolarSystem> observable, SolarSystem oldValue, SolarSystem newValue) {
                starName.setText(newValue.getStar().getStarName());
                star = newValue.getStar();

                starType.setText(star.getType().getName());
                starColor.setText(star.getColor().getName());
                starMass.setText(doubleString(star.getMass()));
                starRadius.setText(doubleString(star.getRadius()));
                starTemp.setText(doubleString(star.getTemp()) + " K");
                starDistance.setText(doubleString(star.getDistance()) + " LY");

                planetList.setItems(FXCollections.observableArrayList(getPlanets()));
                planetList.getSelectionModel().select(0);


                //TODO test drawing

                starViewer.getChildren().clear();

                Circle starC = new Circle(starViewer.getWidth() / 2D, starViewer.getHeight() / 2D, 10);
                starC.setFill(star.getColor().getDisplayColor());

                starViewer.getChildren().add(starC);

                Random r = new Random(solarSystem.getStar().hashCode());

                ArrayList<Planet> planets = solarSystem.getPlanets();

                for (int i = 0; i < planets.size(); i++) {
                    starViewer.getChildren().add(planets.get(i)
                            .getOrbitElipse(i, starViewer.getWidth() / 2D, starViewer.getWidth() / 2D, r.nextInt(360)));
                }

                //TODO refresh starViewer?
                starViewer.requestLayout();
                planetList.getSelectionModel().select(0);
            }
        });
        starTable.getSelectionModel().select(0);

        planetList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null) {
                    return;
                }
                planetName.setText(star.getStarName() + " " + newValue);

                for (Planet pm : solarSystem.getPlanets()) {
                    if (pm.getLetter().equals(newValue)) { //TODO make sure this works
                        planet = pm;
                        break;
                    }
                }
                planetMass.setText(doubleString(planet.getMass()));
                planetRadius.setText(doubleString(planet.getRadius()));
                planetDensity.setText(doubleString(planet.getDensity()));
                planetGoldilocks.setSelected(planet.isGoldilocks());

                orbitRadius.setText(doubleString(planet.getOrbitRadius()));
                orbitPeriod.setText(doubleString(planet.getOrbitPeriod()));
            }
        });
        planetList.setItems(FXCollections.observableArrayList(getPlanets()));
        planetList.getSelectionModel().select(0);
    }

    public void setStage(Stage s) {
        stage = s;
    }


    private ArrayList<SolarSystem> getSolarSystems() {
        try {
            ArrayList<SolarSystem> ss = QueriesWithDBConnection.getSystems(
                    Star.Type.BadFormat,
                    -1, -1,
                    -1, -1,
                    -1, -1);
            starsErrorMessage.setText("");
            return ss;
        } catch (Exception e) {
            starsErrorMessage.setFill(Color.RED);
            starsErrorMessage.setText("Error");
            return new ArrayList<SolarSystem>();
        }
    }

    private ArrayList<String> getPlanets() {
        ArrayList<String> planetNames = new ArrayList<String>();

        if (solarSystem == null)
            return planetNames;

        for (Planet p : solarSystem.getPlanets())
            planetNames.add(p.getLetter());

        return planetNames;
    }

    private String doubleString(Double value) {
        return value != null ? Double.toString(value) : "N/A";
    }

    public void handleFilterChanged() {
       // Call update which will read all the filters and run the query
    }
}
