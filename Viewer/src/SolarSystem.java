//package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The main class
 */
public class SolarSystem {
    private Star star;

    private ArrayList<Planet> planets;

    //TODO verify rs.previous() works as intended.

    /**
     * Extracts the star and planets info from a join of planets and stars
     * increments rs to the end (so that it is on the last tuple for that star)
     *
     * @param rs
     */
    public SolarSystem(ResultSet rs) throws SQLException {
        
        boolean isFirstIteration = true;
        boolean moreRows = false;
        
        while ( (moreRows = rs.next()) ) {
           
            if( isFirstIteration ) {
                
                star = new Star(rs);

                planets = new ArrayList<Planet>();
                
                // no next or star changed.  if star changed moreRows will be true, must revert back one row
                if (!moreRows || !rs.getString("starName").contentEquals(star.getStarName())) {
                
                    break;
                }
                
                planets.add(new Planet(rs));
                
                isFirstIteration = false;
            }
            
            if (!moreRows || !rs.getString("starName").contentEquals(star.getStarName())) {
                
                break;
            }
            

            planets.add(new Planet(rs));

        }

        if (moreRows)
            rs.previous();

    }

    public Star getStar() {
        return star;
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

}
