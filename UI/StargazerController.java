import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;

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
   
   private StarModel star;
   private PlanetModel planet;

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      ObservableList<StarModel> stars = FXCollections.observableArrayList(getStars());

      ArrayList<String> starNames = new ArrayList<String>();

      for (StarModel sm : stars) {
         starNames.add(sm.name);
         // TODO: Remove the lines below this since this list should
         // be populated when the star is created
         sm.planets.add(new PlanetModel("A"));
         sm.planets.add(new PlanetModel("B"));
         sm.planets.add(new PlanetModel("C"));
      }

      starList.setItems(FXCollections.observableArrayList(starNames));

      starList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
         @Override
         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            starName.setText(newValue);
            for (StarModel sm : stars) {
               if (sm.name.equals(newValue)) {
                  star = sm;
                  break;
               }
            }

            starType.setText(star.type);
            starColor.setText(star.color);
            starMass.setText(doubleString(star.mass));
            starRadius.setText(doubleString(star.radius));
            starTemp.setText(doubleString(star.temp));
            starDistance.setText(doubleString(star.distance));
            starGoldilocksInner.setText(doubleString(star.goldilocksInner));
            starGoldilocksOuter.setText(doubleString(star.goldilocksInner));

            planetList.setItems(FXCollections.observableArrayList(getPlanets()));
            planetList.getSelectionModel().select(0);
         }
      });
      starList.getSelectionModel().select(0);

      planetList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
         @Override
         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (newValue == null) {
               return;
            }
            planetName.setText(star.name + " " + newValue);

            for (PlanetModel pm : star.planets) {
               if (pm.name.equals(newValue)) {
                  planet = pm;
                  break;
               }
            }
            planetMass.setText(doubleString(planet.mass));
            planetRadius.setText(doubleString(planet.radius));
            planetDensity.setText(doubleString(planet.density));
            planetGoldilocks.setSelected(planet.goldilocks);

            orbitRadius.setText(doubleString(planet.orbitalRadius));
            orbitPeriod.setText(doubleString(planet.orbitalPeriod));
            orbitEccentricity.setText(doubleString(planet.orbitalEccentricity));
            orbitInclination.setText(doubleString(planet.orbitalInclination));
         }
      });
      planetList.setItems(FXCollections.observableArrayList(getPlanets()));
      planetList.getSelectionModel().select(0);
   }

   // TODO: Must be overriden to construct the stars from the database
   private ArrayList<StarModel> getStars() {
      ArrayList<StarModel> stars = new ArrayList<StarModel>();
      stars.add(new StarModel("11 Com"));
      stars.add(new StarModel("11 UMi"));
      stars.add(new StarModel("14 And"));
      stars.add(new StarModel("14 Her"));

      return stars;
   }

   private ArrayList<String> getPlanets() {
      ArrayList<String> planetNames = new ArrayList<String>();
 
      for (PlanetModel pm : star.planets) {
         planetNames.add(pm.name);
      }

      return planetNames;
   }
   private String doubleString(Double value) {
      return value != null ? Double.toString(value) : "N/A"; 
   }
}
