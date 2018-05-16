import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class Parser {

    public static final String dataFile = "./planets.csv";

    public static void main(String[] args){

        HashSet<String> seenStars = new HashSet<String>();

        String planets = "INSERT INTO planets (starName, letter, orbitalRadius, orbitalPeriod, orbitalEccentricity, " +
                "orbitalInclination, mass, radius, density, goldilocks) VALUES";

        String stars = "INSERT INTO stars (starName, hipName, class, type, color, mass, radius, temp, goldilocksInner, goldilocksOuter, planets, distance) VALUES";

        try {
            BufferedReader read = new BufferedReader(new FileReader(dataFile));
            String line;
            while((line = read.readLine()) != null){

                if(line.startsWith("#"))
                    continue;

                if(line.startsWith("loc_rowid"))
                    continue;

                String[] data = line.split(",(?! )", -1);
                String starName = data[1];
                String letter = data[2];
                int planetNumsTotal = Integer.parseInt(data[4]);
                double period = d(data[5]); // days
                double orbitRadius = d(data[6]); // AU, semi-major axis
                double eccentricity = d(data[7]); // no units
                double inclination = d(data[8]); // degrees
                double mass = d(data[9]); // jupiter masses
                double planetRadius = d(data[11]); //jupiter radii
                double density = d(data[12]); // g/cm^3
                double starDist = d(data[21]); // parsecs
                double starTemp = d(data[25]); // kelvin
                double starMass = d(data[26]); // solar mass
                double starRadius = d(data[27]); // solar radii
                String hipName = data[32];
                String starClass = data[34];

                if(starClass.contains("({"))
                    starClass = starClass.substring(data[34].indexOf("({"));

                String color = "unknown";
                if(starClass.contains("O"))
                    color = "blue";
                else if(starClass.contains("B"))
                    color = "blue white";
                else if(starClass.contains("A"))
                    color = "white";
                else if(starClass.contains("F"))
                    color = "yellow white";
                else if(starClass.contains("G"))
                    color = "yellow";
                else if(starClass.contains("K"))
                    color = "light orange";
                else if(starClass.contains("M"))
                    color = "orange red";

                String type = "unknown";
                if(starClass.contains("I"))
                    type = "supergiant";
                else if (starClass.contains("II"))
                    type = "bright giant";
                else if (starClass.contains("III"))
                    type = "giant";
                else if (starClass.contains("IV"))
                    type = "subgiant";
                else if (starClass.contains("V"))
                    type = "main sequence";

                //TODO calculate using magnitude http://www.planetarybiology.com/calculating_habitable_zone.html
                double goldInner = -1;
                double goldOuter = -1;
                if(starTemp != -1 && starRadius != -1){
                    double lumosity = 4 * Math.PI * Math.pow(starRadius * 695700000, 2) * 5.67 * Math.pow(10, -8) * Math.pow(starTemp, 4);
                    lumosity /= 3.828 * Math.pow(10, 26);
                    goldInner = Math.sqrt(lumosity / 1.1);
                    goldOuter = Math.sqrt(lumosity / 0.53);
                }

                if(!seenStars.contains(starName)){
                    stars += String.format("\n\t('%s', '%s', '%s', '%s', '%s', %f, %f, %f, %f, %f, %d, %f), ",
                            starName, hipName, starClass, type, color, starMass, starRadius, starTemp,
                            goldInner, goldOuter, planetNumsTotal, starDist);
                    seenStars.add(starName);
                }

                planets += String.format("\n\t('%s', '%s', %f, %f, %f, %f, %f, %f, %f, %d), ",
                        starName, letter, orbitRadius, period, eccentricity, inclination, mass, planetRadius, density,
                        (orbitRadius != -1 && orbitRadius >= goldInner && orbitRadius <= goldOuter) ? 1 : 0);


            }

            planets = planets.substring(0, planets.length() - 2) + ";";
            stars = stars.substring(0, stars.length() - 2) + ";";

            FileWriter fw = new FileWriter("./parsed.sql");
            fw.write(stars);
            fw.write("\n\n");
            fw.write(planets);
            fw.close();

            read.close();

        } catch( Exception e ){
            e.printStackTrace();
        }


    }

    public static double d(String s){
        if(s == null || s.trim().contentEquals(""))
            return -1;
        else
            return Double.parseDouble(s);
    }

}
