import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private Label planetName;
    @FXML
    private TextField planetMass;
    @FXML
    private TextField planetRadius;
    @FXML
    private TextField planetDensity;
    @FXML
    private CheckBox planetGoldilocks;

    @FXML
    private TextField orbitRadius;
    @FXML
    private TextField orbitPeriod;
    @FXML
    private TextField orbitEccentricity;
    @FXML
    private TextField orbitInclination;

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

    private Star star;
    private Planet planet;

    private static final int starViewerWidth = 480;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileChooser = new FileChooser();

        nameColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, String>("name"));
        planetColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Integer>("planetCount"));
        goldilocksColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Integer>("goldilocks"));

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
                    starTable.refresh();
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

                planetList.setItems(FXCollections.observableArrayList(getPlanets(newValue)));
                planetList.getSelectionModel().select(0);


                //TODO test drawing


                starViewer.requestLayout();
                starViewer.getChildren().clear();


                //TODO starViewer height and width are not set when this first runs
                Circle starC = new Circle(starViewerWidth / 2D, starViewerWidth / 2D, 10);
                starC.setFill(star.getColor().getDisplayColor());

                starViewer.getChildren().add(starC);

                Random r = new Random(newValue.getStar().hashCode());

                ArrayList<Planet> planets = newValue.getPlanets();

                for (int i = 0; i < planets.size(); i++) {
                    starViewer.getChildren().add(planets.get(i)
                            .getOrbitElipse(newValue.getOrbitDrawFactor(starViewerWidth), i, starViewerWidth / 2D, starViewerWidth / 2D, r.nextInt(360)));
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

                for (Planet pm : starTable.getSelectionModel().getSelectedItem().getPlanets()) {
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
                orbitEccentricity.setText(doubleString(planet.getOrbitEccentricity()));
                orbitInclination.setText(doubleString(planet.getOrbitInclination()));
            }
        });
        planetList.setItems(FXCollections.observableArrayList(new ArrayList<String>()));
        planetList.getSelectionModel().select(0);
    }

    public void setStage(Stage s) {
        stage = s;
    }


    private ArrayList<SolarSystem> getSolarSystems() {
        try {
            ArrayList<SolarSystem> ss = QueriesWithDBConnection.getSystems(
                    Star.Type.Supergiant,
                    2, -1,
                    1, -1,
                    -1, -1);
            starsErrorMessage.setText("");
            return ss;
        } catch (Exception e) {
            starsErrorMessage.setFill(Color.RED);
            starsErrorMessage.setText("Error");
            return new ArrayList<SolarSystem>();
        }
    }

    private ArrayList<String> getPlanets(SolarSystem solarSystem) {
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
}
