import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.util.Comparator;
import java.util.Random;
import java.util.ResourceBundle;

public class StargazerController implements Initializable {
    @FXML
    private ListView<Planet> planetList;
    @FXML
    private TableView<SolarSystem> starTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn planetColumn;
    @FXML
    private TableColumn goldilocksColumn;
    @FXML
    private TableColumn distanceColumn;

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
    private TextField starClass;

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
    private Label viewerAU;

    @FXML
    private Text starsErrorMessage;

    @FXML
    private Text importErrorMessage;

    private FileChooser fileChooser = null;
    private Stage stage;

    private static final int starViewerWidth = 480;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileChooser = new FileChooser();

        nameColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, String>("name"));
        planetColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Integer>("planetCount"));
        goldilocksColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Integer>("goldilocks"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<SolarSystem, Double>("distance"));

        distanceColumn.setComparator(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                if (o1 < 0 && o2 >= 0)
                    return 100;
                else if (o2 < 0 && o1 >= 0)
                    return -100;
                else if (o1 < 0 && o2 < 0)
                    return 0;
                else return o1.compareTo(o2);
            }
        });

        typeSelect.getItems().addAll(
           "All",
           "Unknown",
                "Supergiant",
           "Bright Giant",
           "Giant",
           "Subgiant",
           "Main Sequence"
        );
        planetList.setCellFactory(param -> new ListCell<Planet>() {
            @Override
            protected void updateItem(Planet item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("");
                    setStyle("-fx-control-inner-background: derive(-fx-base, 80%);");
                }
                else {
                    setText(item.getLetter());

                    if (item.isGoldilocks()) {
                        setStyle("-fx-control-inner-background: derive(green, 80%);");
                    } else {
                        setStyle("-fx-control-inner-background: derive(-fx-base, 80%);");
                    }
                }


            }
        });

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

                    importErrorMessage.setFill(Color.GREEN);
                    importErrorMessage.setText("Done");
                } catch (Exception e) {
                    e.printStackTrace();

                    if (e.getMessage().contains("Duplicate entry")) {
                        importErrorMessage.setFill(Color.RED);
                        importErrorMessage.setText("Error: Duplicate Entry");
                        return;
                    }

                    importErrorMessage.setFill(Color.RED);
                    importErrorMessage.setText("Error");
                }

            }
        });

        starTable.setItems(FXCollections.observableArrayList(getSolarSystems()));

        starTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SolarSystem>() {
            @Override
            public void changed(ObservableValue<? extends SolarSystem> observable, SolarSystem oldValue, SolarSystem newValue) {

                if (newValue == null)
                    return;

                Star star = newValue.getStar();

                starName.setText(star.getStarName());
                starType.setText(star.getType().getName());
                starColor.setText(star.getColor().getName());
                starMass.setText(doubleString(star.getMass()));
                starRadius.setText(doubleString(star.getRadius()));
                starTemp.setText(doubleString(star.getTemp()));
                starDistance.setText(doubleString(star.getDistance()));
                starClass.setText(star.getStarClass());

                viewerAU.setText((starViewerWidth / (2D * newValue.getOrbitDrawFactor(starViewerWidth))) + " AU");

                planetList.getItems().clear();
                planetList.setItems(FXCollections.observableArrayList(newValue.getPlanets()));

                for (int i = 0; i < newValue.getPlanetCount(); i++) {
                    if (newValue.getPlanets().get(i).isGoldilocks()) {
                        planetList.itemsProperty();
                    }
                }

                planetList.getSelectionModel().select(0);

                starViewer.getChildren().clear();

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
            }
        });
        starTable.getSelectionModel().select(0);

        planetList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Planet>() {
            @Override
            public void changed(ObservableValue<? extends Planet> observable, Planet oldValue, Planet newValue) {
                if (newValue == null) {
                    return;
                }
                planetName.setText(starTable.getSelectionModel().getSelectedItem().getStar().getStarName() + " " + newValue.getLetter());
                planetMass.setText(doubleString(newValue.getMass()));
                planetRadius.setText(doubleString(newValue.getRadius()));
                planetDensity.setText(doubleString(newValue.getDensity()));
                orbitRadius.setText(doubleString(newValue.getOrbitRadius()));
                orbitPeriod.setText(doubleString(newValue.getOrbitPeriod()));
                planetGoldilocks.setSelected(newValue.isGoldilocks());
            }
        });
        planetList.getSelectionModel().select(1);
        planetList.getSelectionModel().select(0);
    }

    public void setStage(Stage s) {
        stage = s;
    }


    private int parseFilter(TextField filter) {
        if (filter.getText().trim().equals(""))
            return -1;

        try {
            return (int) Double.parseDouble(filter.getText());
        } catch (Exception e) {

            return -1;
        }

    }

    private Star.Type parseComboBox(ComboBox cb) {

        if (cb == null || cb.getSelectionModel().getSelectedItem() == null)
            return Star.Type.BadFormat;

        String name = cb.getSelectionModel().getSelectedItem().toString();

        if (name.equals("All") || name.trim().equals("")) {
            return Star.Type.BadFormat;
        }

        for (Star.Type t : Star.Type.values()) {
            if (t.getName().equals(name))
                return t;
        }

        return Star.Type.BadFormat;
    }

    private ArrayList<SolarSystem> getSolarSystems() {
        try {
            ArrayList<SolarSystem> ss = QueriesWithDBConnection.getSystems(
                    parseComboBox(typeSelect),
                    parseFilter(minPlanets), parseFilter(maxPlanets),
                    parseFilter(minGoldilocks), parseFilter(maxGoldilocks),
                    (int) (parseFilter(minDistance) / 3.26D), (int) (parseFilter(maxDistance) / 3.26D));
            starsErrorMessage.setText("");

            ss.sort(new Comparator<SolarSystem>() {
                @Override
                public int compare(SolarSystem o1, SolarSystem o2) {
                    if (o1.getDistance() < 0 && o2.getDistance() >= 0)
                        return 100;
                    else if (o2.getDistance() < 0 && o1.getDistance() >= 0)
                        return -100;
                    else if (o1.getDistance() < 0 && o2.getDistance() < 0)
                        return 0;
                    else return o1.getDistance().compareTo(o2.getDistance());
                }
            });

            return ss;
        } catch (Exception e) {
            e.printStackTrace();
            starsErrorMessage.setFill(Color.RED);
            starsErrorMessage.setText("Error");
            return new ArrayList<SolarSystem>();
        }
    }

    private String doubleString(Double value) {
        return (value != null && value >= 0) ? Double.toString(value) : "N/A";
    }

    public void handleFilterChanged() {
        starTable.setItems(FXCollections.observableArrayList(getSolarSystems()));
        starTable.getSelectionModel().select(0);
    }
}
