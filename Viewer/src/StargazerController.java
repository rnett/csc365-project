import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StargazerController implements Initializable {
   @FXML private ListView<String> planetList;
   @FXML private ListView<String> starList;

   @FXML private Label starName;
   @FXML private TextField starType;
   @FXML private TextField starColor;
   @FXML private TextField starMass;
   @FXML private TextField starRadius;
   @FXML private TextField starTemp;
   @FXML private TextField starDistance;
   @FXML private TextField starGoldilocksInner;
   @FXML private TextField starGoldilocksOuter;

   @FXML private Label planetName;
   @FXML private TextField planetMass;
   @FXML private TextField planetRadius;
   @FXML private TextField planetDensity;
   @FXML private CheckBox planetGoldilocks;

   @FXML private TextField orbitRadius;
   @FXML private TextField orbitPeriod;
   @FXML private TextField orbitEccentricity;
   @FXML private TextField orbitInclination;

    @FXML
    private Canvas canvas;

    private SolarSystem solarSystem;
    private Star star;
    private Planet planet;

   @Override
   public void initialize(URL location, ResourceBundle resources) {
       ObservableList<SolarSystem> stars = FXCollections.observableArrayList(getSolarSystems());

      ArrayList<String> starNames = new ArrayList<String>();

       for (SolarSystem sm : stars) {
           starNames.add(sm.getStar().getStarName());
         // TODO: Remove the lines below this since this list should
           // be populated when the solarSystem is created
      }

      starList.setItems(FXCollections.observableArrayList(starNames));

      starList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
         @Override
         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            starName.setText(newValue);
             for (SolarSystem sm : stars) {
                 if (sm.getStar().getStarName().equals(newValue)) {
                     solarSystem = sm;
                     star = solarSystem.getStar();
                  break;
               }
            }

             starType.setText(star.getType().getName());
             starColor.setText(star.getColor().getName());
             starMass.setText(doubleString(star.getMass()));
             starRadius.setText(doubleString(star.getRadius()));
             starTemp.setText(doubleString(star.getTemp()) + " K");
             starDistance.setText(doubleString(star.getDistance()) + " LY");
             starGoldilocksInner.setText(doubleString(star.getGoldilocksInner()) + " AU");
             starGoldilocksOuter.setText(doubleString(star.getGoldilocksOuter()) + " AU");

            planetList.setItems(FXCollections.observableArrayList(getPlanets()));
            planetList.getSelectionModel().select(0);

             //TODO drawing, DON'T NEED CANVAS, CAN DRAW ONTO HBOX ETC
             GraphicsContext gc = canvas.getGraphicsContext2D();
             Circle starC = new Circle(canvas.getWidth() / 2D, canvas.getHeight() / 2D, 10);

             for (Planet p : solarSystem.getPlanets()) {
                 p.getOrbitElipse(canvas.getWidth() / 2D, canvas.getWidth() / 2D, 0);
             }

         }
      });
      starList.getSelectionModel().select(0);

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
             orbitEccentricity.setText(doubleString(planet.getOrbitEccentricity()));
             orbitInclination.setText(doubleString(planet.getOrbitInclination()));
         }
      });
      planetList.setItems(FXCollections.observableArrayList(getPlanets()));
      planetList.getSelectionModel().select(0);
   }

   // TODO: Must be overriden to construct the stars from the database
   private ArrayList<SolarSystem> getSolarSystems() {
       ArrayList<SolarSystem> stars = new ArrayList<SolarSystem>();

      return stars;
   }

   private ArrayList<String> getPlanets() {
      ArrayList<String> planetNames = new ArrayList<String>();

      return planetNames;
   }
   private String doubleString(Double value) {
      return value != null ? Double.toString(value) : "N/A"; 
   }
}
