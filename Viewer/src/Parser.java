import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class Parser {


    /* uses star mass and temp to calculate goldilocks zone, then magnitude
       assuming magnitude will have more error (interstelar dust, etc) than mass and temp
    */
    public static void main(String[] args) {
        QueriesWithDBConnection.connect(args[0], args[1], args[2]);
        try {
            parse(args[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        QueriesWithDBConnection.close();
    }

    public static void parse(String dataFile) throws Exception {
        HashSet<String> seenStars = new HashSet<String>();

        String planets = "INSERT INTO planets (starName, letter, orbitalRadius, orbitalPeriod, orbitalEccentricity, " +
                "orbitalInclination, planetMass, planetRadius, density, goldilocks) VALUES";

        String stars = "INSERT INTO stars (starName, hipName, class, type, color, starMass, starRadius, temp, goldilocksInner, goldilocksOuter, planets, distance) VALUES";


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
                double magnitude = d(data[22]);

                //TODO convert mass & radius to earth instead of jupiter

                if (eccentricity == -1)
                    eccentricity = 0;

                if (inclination == -1)
                    inclination = 0;

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


                double goldInner1 = -1;
                double goldOuter1 = -1;
                if(starTemp != -1 && starRadius != -1){
                    double lumosity = 4 * Math.PI * Math.pow(starRadius * 695700000, 2) * 5.67 * Math.pow(10, -8) * Math.pow(starTemp, 4);
                    lumosity /= 3.828 * Math.pow(10, 26);
                    goldInner1 = Math.sqrt(lumosity / 1.1);
                    goldOuter1 = Math.sqrt(lumosity / 0.53);

                }

                double goldInner2 = -1;
                double goldOuter2 = -1;
                if (magnitude != -1 && starDist != -1 && !color.contentEquals("unknown")) {

                    double BC = -0.4;
                    switch (color) {
                        case "blue":
                            BC = -4.3;
                            break;
                        case "blue white":
                            BC = -2.0;
                            break;
                        case "white":
                            BC = -0.3;
                            break;
                        case "yellow white":
                            BC = -0.15;
                            break;
                        case "yellow":
                            BC = -0.4;
                            break;
                        case "light orange":
                            BC = -0.8;
                            break;
                        case "orange red":
                            BC = -2.0;
                            break;
                        default:
                            break;
                    }

                    double absMag = magnitude - 5 * Math.log10(starDist / 10);
                    double bolMag = absMag + BC;
                    double lumosity = Math.pow(10, (bolMag - 4.72) / -2.5);
                    goldInner2 = Math.sqrt(lumosity / 1.1);
                    goldOuter2 = Math.sqrt(lumosity / 0.53);

                }

                double goldInner = goldInner1;
                double goldOuter = goldOuter1;
                /*
                if(goldInner1 != -1 && goldInner2 != -1){

                    goldInner = (goldInner1 + goldInner2) / 2D;
                    goldOuter = (goldOuter1 + goldOuter2) / 2D;

                    System.out.println(String.format("Both are good\nDiffs: %5f\n%5f : %5f",
                            100*(2 * (goldInner1 - goldInner2)/(goldInner1 + goldInner2)),
                            goldInner1, goldInner2));


                } else if(goldInner1 != -1 && goldInner2 == -1){
                    goldInner = goldInner1;
                    goldOuter = goldOuter1;
                } else if(goldInner1 == -1 && goldInner2 != -1){
                    goldInner = goldInner2;
                    goldOuter = goldOuter2;
                }
                */
                if (goldInner == -1) {
                    goldInner = goldInner2;
                    goldOuter = goldOuter2;
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

            /*
            FileWriter fw = new FileWriter("./ParsedData.sql");//TODO need to run data import
            fw.write(stars);
            fw.write("\n\n");
            fw.write(planets);
            fw.close();
            */

            read.close();
            //TODO add `number of glodilocks planets` by query.  leave out goldilocks t/f and find in query?
            Connection c = QueriesWithDBConnection.connect();

        try {

            c.setAutoCommit(false);

            Statement s1 = c.createStatement();
            s1.executeLargeUpdate(stars);
            s1.close();

            Statement s2 = c.createStatement();
            s2.executeLargeUpdate(planets);
            s2.close();

            c.commit();

            c.setAutoCommit(true);
        } catch (SQLException se) {
            c.close();
            throw se;
        }

    }

    public static double d(String s){
        if(s == null || s.trim().contentEquals(""))
            return -1;
        else
            return Double.parseDouble(s);
    }

}
