import java.sql.ResultSet;
import java.sql.SQLException;

public class Star {

    private String starName;
    private String hipName;
    private String starClass;
    private Type type;
    private Color color;
    private double mass;
    private double radius;
    private double temp;
    private double goldilocksInner;
    private double goldilocksOuter;
    private int planets;
    private double distance;

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
        //TODO type
        //TODO color
        mass = rs.getDouble("mass");
        radius = rs.getDouble("radius");
        temp = rs.getDouble("temp");
        goldilocksInner = rs.getDouble("goldilocksInner");
        goldilocksOuter = rs.getDouble("goldilocksOuter");
        planets = rs.getInt("planets");
        distance = rs.getDouble("distance"); //TODO use ly?
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
        Unknown("Unknown"),
        Blue("Blue"),
        BlueWhite("Blue White"),
        White("White"),
        YellowWhite("Yellow White"),
        Yellow("Yellow"),
        LightOrange("Light Orange"),
        OrangeRed("Orange Red");

        private String name;

        Color(String str) {
            name = str;
        }

        public String getName() {
            return name;
        }
    }

    public enum Type {
        Unknown("Unknown"),
        Supergiant("Supergiant"),
        BrightGiant("Bright Giant"),
        Giant("Giant"),
        Subgiant("Subgiant"),
        MainSequence("Main Sequence");

        private String name;

        Type(String str) {
            name = str;
        }

        public String getName() {
            return name;
        }
    }

}
