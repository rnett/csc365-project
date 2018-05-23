import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The main class
 */
public class SolarSystem {
    private Star star;

    private ArrayList<Planet> planets;

    //TODO test this

    /**
     * Extracts the star and planets info from a join of planets and stars
     * increments rs to the end (so that it is on the last tuple for that star)
     *
     * @param rs
     */
    public SolarSystem(ResultSet rs) throws SQLException {
        star = new Star(rs);

        planets = new ArrayList<Planet>();

        planets.add(new Planet(rs));

        boolean ended = false;

        while (true) {
            ended = !rs.next();

            if (ended || !rs.getString("starName").contentEquals(star.getStarName())) {
                // no next or star changed.  if star changed ended will be false
                break;
            }

            planets.add(new Planet(rs));

        }

        if (!ended)
            rs.previous();

    }

    public Star getStar() {
        return star;
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

}
