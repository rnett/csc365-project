import java.sql.ResultSet;

public class Planet {

    private String starName;
    private String letter;
    private double orbitRadius;
    private double orbitPeriod;
    private double orbitEccentricity;
    private double orbitInclination;
    private double mass;
    private double radius;
    private double density;
    private boolean goldilocks;

    /**
     * Reads a Star from the ResultSet.  Must have all Star fields.
     * Does not change ResultSet position
     *
     * @param rs
     */
    public Planet(ResultSet rs) {
        //TODO
    }

    public String getStarName() {
        return starName;
    }

    public String getLetter() {
        return letter;
    }

    public double getOrbitRadius() {
        return orbitRadius;
    }

    public double getOrbitPeriod() {
        return orbitPeriod;
    }

    public double getOrbitEccentricity() {
        return orbitEccentricity;
    }

    public double getOrbitInclination() {
        return orbitInclination;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public double getDensity() {
        return density;
    }

    public boolean isGoldilocks() {
        return goldilocks;
    }

    public String getPlanetName() {
        return starName + " " + letter;
    }
}
