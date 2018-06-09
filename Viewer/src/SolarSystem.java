//package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The main class
 */
public class SolarSystem {
    private Star star;

    private final SimpleStringProperty name;
    private final SimpleIntegerProperty planets;
    private final SimpleIntegerProperty goldilocks;
    private ArrayList<Planet> _planets;

    //TODO verify rs.previous() works as intended.
    private int _golds = -1;

    public Star getStar() {
        return star;
    }

    /**
     * Extracts the star and planets info from a join of planets and stars
     * increments rs to the end (so that it is on the last tuple for that star)
     *
     * @param rs is assumed to be a natural join of stars and planets table; every attribute
     *  is projected. starName can never be null after natural joining.
     */
    public SolarSystem(ResultSet rs) throws SQLException {

        boolean isFirstIteration = true; //used to get the star of a solar system
        boolean moreRows = false;

        while ( (moreRows = rs.next()) ) {

            if( isFirstIteration ) {

                star = new Star(rs);

                _planets = new ArrayList<Planet>();

                _planets.add(new Planet(rs));

                isFirstIteration = false;
            } else {

                // no next or star changed.
                //If star changed moreRows will be true, must revert back one row in preparation for next SolarSystem creation
                // if starName is different, that implies current planet is not part of current solar system.
                if (!moreRows || !rs.getString("starName").contentEquals(star.getStarName())) {

                    break;
                }


                _planets.add(new Planet(rs));

                }

        }

        if (moreRows)
            rs.previous();

        this.name = new SimpleStringProperty(getStar().getStarName());
        this.planets = new SimpleIntegerProperty(getPlanets().size());
        this.goldilocks = new SimpleIntegerProperty(getNumberGoldilocksPlanets());

    }

    public ArrayList<Planet> getPlanets() {
        return _planets;
    }

    public int getNumberGoldilocksPlanets() {
        if (_golds == -1) {
            int c = 0;
            for (Planet p : _planets) {
                if (p.isGoldilocks())
                    c++;
            }

            _golds = c;
        }

        return _golds;
    }

}
