import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public Planet(ResultSet rs) throws SQLException {
        starName = rs.getString("starName");
        letter = rs.getString("letter");
        orbitRadius = rs.getDouble("orbitRadius");
        orbitPeriod = rs.getDouble("orbitPeriod");
        orbitEccentricity = rs.getDouble("orbitalEccentricity");
        orbitInclination = rs.getDouble("orbitalInclination");
        mass = rs.getDouble("planetMass");
        radius = rs.getDouble("planetRadius");
        density = rs.getDouble("density");
        goldilocks = rs.getBoolean("goldilocks");
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

    public double getMinorOrbitRadius() {
        return orbitRadius * Math.sqrt(1 - Math.pow(orbitEccentricity, 2));
    }

    public double getLinearEcentricity() {
        return Math.sqrt(Math.pow(orbitRadius, 2) - Math.pow(getMinorOrbitRadius(), 2));
    }

    public Ellipse getOrbitElipse(double starX, double starY, double angle) {
        Ellipse e = new Ellipse(starX + getLinearEcentricity(), starX, getOrbitRadius(), getMinorOrbitRadius());
        Rotate r = new Rotate();
        r.setPivotX(starX);
        r.setPivotY(starY);
        r.setAngle(angle);

        e.getTransforms().add(r);

        return e;
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