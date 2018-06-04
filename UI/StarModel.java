import java.util.ArrayList;

public class StarModel {
   public String name;
   public String type;
   public String color;
   public Double mass;
   public Double radius;
   public Double temp;
   public Double distance;
   public Double goldilocksInner;
   public Double goldilocksOuter;
   public ArrayList<PlanetModel> planets;

   // Should take all values as arguments but I'm using constants
   // to make my life easier.
   public StarModel(String starName) {
      name = starName;
      type = "supergiant";
      color = "light orange";
      mass = 2.7;
      radius = 19.0;
      temp = 4742.0;
      distance = 110.62;
      goldilocksInner = 12.226801;
      goldilocksOuter = 17.614538;

      //TODO: This needs to be overridden to get planets by star
      planets = new ArrayList<PlanetModel>();
   }
}