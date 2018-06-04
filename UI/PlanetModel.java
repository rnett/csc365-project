public class PlanetModel {
   public String name;
   public Double mass;
   public Double radius;
   public Double density;
   public boolean goldilocks;
   public Double orbitalRadius;
   public Double orbitalPeriod;
   public Double orbitalEccentricity;
   public Double orbitalInclination;

   // Should take all values as arguments but I'm using constants
   // to make my life easier.
   public PlanetModel(String planetName) {
      name = planetName;
      mass = 19.4;
      radius = null;
      density = null;
      goldilocks = false;

      orbitalRadius = 1.29;
      orbitalPeriod = 326.03;
      orbitalEccentricity = 0.231;
      orbitalInclination = 19.0;
   }
}