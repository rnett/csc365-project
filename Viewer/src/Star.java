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
        Unknown("Unknown", javafx.scene.paint.Color.GRAY, "unknown"),
        Blue("Blue", javafx.scene.paint.Color.BLUE, "blue"),
        BlueWhite("Blue White", javafx.scene.paint.Color.LIGHTBLUE, "blue white"),
        White("White", javafx.scene.paint.Color.WHITE, "white"),
        YellowWhite("Yellow White", javafx.scene.paint.Color.LIGHTYELLOW, "yellow white"),
        Yellow("Yellow", javafx.scene.paint.Color.YELLOW, "yellow"),
        LightOrange("Light Orange", javafx.scene.paint.Color.ORANGE, "light orange"),
        OrangeRed("Orange Red", javafx.scene.paint.Color.ORANGERED, "orange red"),
        BadFormat("Bad Format in DB", javafx.scene.paint.Color.GRAY, "");

        private String name;
        private javafx.scene.paint.Color color;
        private String db;

        Color(String str, javafx.scene.paint.Color displayColor, String db) {
            name = str;
            color = displayColor;
            this.db = db;
        }

        public javafx.scene.paint.Color getDisplayColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public static Color fromDB(String data) {
            for (Color c : Color.values()) {
                if (c.db == data)
                    return c;
            }
            return BadFormat;
        }

        public String toDB() {
            return db;
        }
    }

    public enum Type {
        BadFormat("Bad Format in DB", ""),
        Unknown("Unknown", "unknown"),
        Supergiant("Supergiant", "supergiant"),
        BrightGiant("Bright Giant", "bright giant"),
        Giant("Giant", "giant"),
        Subgiant("Subgiant", "subgiant"),
        MainSequence("Main Sequence", "main sequence");

        private String name;
        private String db;

        Type(String str, String db) {
            name = str;
            this.db = db;
        }

        public String getName() {
            return name;
        }

        public static Type fromDB(String data) {
            for (Type t : Type.values()) {
                if (t.db.equals(data))
                    return t;
            }
            return BadFormat;
        }

        public String toDB() {
            return db;
        }
    }

}
