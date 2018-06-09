//package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Planet {

    private String starName;
    private String letter;
    private double orbitRadius; //TODO AU
    private double orbitPeriod; // days
    private double orbitEccentricity;
    private double orbitInclination;
    private double mass; //TODO earth masses
    private double radius; //TODO earth radii
    private double density; // g/cm3
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
        orbitRadius = rs.getDouble("orbitalRadius");
        orbitPeriod = rs.getDouble("orbitalPeriod");
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

    public Ellipse getOrbitElipse(double factor, int index, double starX, double starY, double angle) {
        Ellipse e = new Ellipse(starX + getLinearEcentricity(), starX, getOrbitRadius() * factor, getMinorOrbitRadius() * factor);

        e.setStroke(getColor(index));
        e.setFill(Color.TRANSPARENT);

        if (goldilocks) {
            e.setStrokeWidth(2);
        }

        Rotate r = new Rotate();
        r.setPivotX(starX);
        r.setPivotY(starY);
        r.setAngle(angle);

        e.getTransforms().add(r);

        return e;
    }

    public Color getColor(int index) {

        if (this.goldilocks)
            return Color.LIGHTGREEN;

        switch (index) {
            case 0:
                return Color.WHITE;
            case 1:
                return Color.RED;
            case 2:
                return Color.ORANGE;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.DARKGREEN;
            case 5:
                return Color.CYAN;
            case 6:
                return Color.BLUE;
            case 7:
                return Color.PURPLE;
            case 8:
                return Color.GREY;

            default:
                return Color.BLACK;
        }
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
