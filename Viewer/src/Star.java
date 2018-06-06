//package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import java.sql.ResultSet;
import java.sql.SQLException;

public class Star {

    private String starName;
    private String hipName;
    private String starClass;
    private Type type;
    private Color color;
    private double mass; // solar masses
    private double radius; // solar radius
    private double temp; // kelvin
    private double goldilocksInner;
    private double goldilocksOuter;
    private int planets;
    private double distance; // light years

    /**
     * Reads a Star from the ResultSet.  Must have all Star fields.
     * Does not change ResultSet position
     *
     * @param rs
     */
    public Star(ResultSet rs) throws SQLException {
        starName = rs.getString("starName");
        hipName = rs.getString("hipName");
        starClass = rs.getString("class");
        type = Type.fromDB(rs.getString("type"));
        color = Color.fromDB(rs.getString("color"));
        mass = rs.getDouble("starMass");
        radius = rs.getDouble("starRadius");
        temp = rs.getDouble("temp");
        goldilocksInner = rs.getDouble("goldilocksInner");
        goldilocksOuter = rs.getDouble("goldilocksOuter");
        planets = rs.getInt("planets");
        distance = rs.getDouble("distance") * 3.26D; // converted to ly
    }

    public String getStarName() {
        return starName;
    }

    public String getHipName() {
        return hipName;
    }

    public String getStarClass() {
        return starClass;
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public double getTemp() {
        return temp;
    }

    public double getGoldilocksInner() {
        return goldilocksInner;
    }

    public double getGoldilocksOuter() {
        return goldilocksOuter;
    }

    public int getNumPlanets() {
        return planets;
    }

    public double getDistance() {
        return distance;
    }

    public enum Color {
        Unknown("Unknown", javafx.scene.paint.Color.GRAY),
        Blue("Blue", javafx.scene.paint.Color.BLUE),
        BlueWhite("Blue White", javafx.scene.paint.Color.LIGHTBLUE),
        White("White", javafx.scene.paint.Color.WHITE),
        YellowWhite("Yellow White", javafx.scene.paint.Color.LIGHTYELLOW),
        Yellow("Yellow", javafx.scene.paint.Color.YELLOW),
        LightOrange("Light Orange", javafx.scene.paint.Color.ORANGE),
        OrangeRed("Orange Red", javafx.scene.paint.Color.ORANGERED),
        BadFormat("Bad Format in DB", javafx.scene.paint.Color.GRAY);

        private String name;
        private javafx.scene.paint.Color color;

        Color(String str, javafx.scene.paint.Color displayColor) {
            name = str;
            color = displayColor;
        }

        public javafx.scene.paint.Color getDisplayColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public static Color fromDB(String data) {
            switch (data) {
                case "unknown":
                    return Unknown;
                case "blue":
                    return Blue;
                case "blue white":
                    return BlueWhite;
                case "white":
                    return White;
                case "yellow white":
                    return YellowWhite;
                case "yellow":
                    return Yellow;
                case "light orange":
                    return LightOrange;
                case "orange red":
                    return OrangeRed;
                default:
                    return BadFormat;
            }
        }
    }

    public enum Type {
        Unknown("Unknown"),
        Supergiant("Supergiant"),
        BrightGiant("Bright Giant"),
        Giant("Giant"),
        Subgiant("Subgiant"),
        MainSequence("Main Sequence"),
        BadFormat("Bad Format in DB");

        private String name;

        Type(String str) {
            name = str;
        }

        public String getName() {
            return name;
        }

        public static Type fromDB(String data) {
            switch (data) {
                case "unknown":
                    return Unknown;
                case "supergiant":
                    return Supergiant;
                case "bright giant":
                    return BrightGiant;
                case "giant":
                    return Giant;
                case "subgiant":
                    return Subgiant;
                case "main sequence":
                    return MainSequence;
                default:
                    return BadFormat;
            }
        }
    }

}
