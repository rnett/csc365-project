//package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class QueriesWithDBConnection {

    private static Connection connect;

    private static Session sshSession = null;

    private static boolean useArgs = false;
    private static String sshHost;
    private static String sshUser;
    private static String sshPW;

    public static Connection connect(String host, String user, String pw) {

        sshHost = host;
        sshUser = user;
        sshPW = pw;
        useArgs = true;

        return connect();
    }

    public static Connection connect() {
        try {
            if (connect == null || connect.isClosed()) {

                connect = newConnect();
            }
        } catch (Exception e) {
            connect = newConnect();
        }

        return connect;
    }

    /*
    Filtering queries involving one or more attributes.
    
    supported comparison operators: <=, =, >=
    supported values: double, int, String
    supported logical operators (chainOps): and, or
    
    Returns information on all solar systems (system with a star and at least one planet orbiting around it).
    Otherwise, returns empty list of solar systems on invalid SQL statements or if no filters are used.
    */
    public static ArrayList<SolarSystem> getStarsFilterByMultipleAttr(ArrayList<String> attributes,
                                                                      ArrayList<String> compareOps,
                                                                      ArrayList values, ArrayList<String> chainOps) throws SQLException {

        ResultSet rs = null;
        ArrayList<SolarSystem> allSys = new ArrayList<SolarSystem>();

        //building SQL statement to be executed

            Statement statement = connect().createStatement();

        String sqlStatement = "select * from stargazers.solarSystems";

            if (attributes.isEmpty() || compareOps.isEmpty() ||
                    ((attributes.size() != compareOps.size()) && (compareOps.size() != values.size())) ||
                    chainOps.size() != attributes.size() - 1) {
                return allSys;
            }

            if(attributes.size() > 0) {
                sqlStatement += " where ";
            }

            for(int i = 0; i < attributes.size(); i++) {

                if( compareOps.get(i) != "<=" && compareOps.get(i) != "=" &&  compareOps.get(i) != ">=" ) {
                    System.out.println("Unsupported comparison operator.");
                    return allSys;
                }

                if( (values.get(i) instanceof Double || values.get(i) instanceof Integer) ) {

                    if(!chainOps.isEmpty()) {
                        sqlStatement += attributes.get(i) + " " + compareOps.get(i) + " " + values.get(i) + " " + chainOps.remove(0) + " ";
                    } else {
                        sqlStatement += attributes.get(i) + " " + compareOps.get(i) + " " + values.get(i);
                    }

                } else if(values.get(i) instanceof String) {
                    
                    if(!chainOps.isEmpty()) {
                        sqlStatement += attributes.get(i) + " " + compareOps.get(i) + " \'" + values.get(i) + "\' " + chainOps.remove(0) + " ";
                    } else {
                        sqlStatement += attributes.get(i) + " " + compareOps.get(i) + " \'" + values.get(i) + "\'";
                    }
                    
                } else {
                    System.out.println("Value needs to be either a string, double or integer.");
                    return allSys;
                }


            }


            rs = statement.executeQuery(sqlStatement); //ResultSet is an iterator

            System.out.println("\nQuery executed: " + sqlStatement);


        while( !rs.isAfterLast() )
                allSys.add(new SolarSystem(rs) );

        
        return allSys;
    }
    
    public static ArrayList<SolarSystem> getSystems(Star.Type type, 
            int planetsMin, int planetsMax, 
            int goldilocksMin, int goldilocksMax, 
            int distanceMin, int distanceMax) {

        ArrayList<String> attributes = new ArrayList<String>();
        ArrayList<String> compOps = new ArrayList<String>();
        ArrayList<Object> values = new ArrayList<Object>();
        ArrayList<String> logicalOps = new ArrayList<String>();
        String and = "and";

        ArrayList<SolarSystem> allSys = null;

        if (type != Star.Type.BadFormat) {
            attributes.add("type");
            compOps.add("=");
            values.add(type.toDB());
            logicalOps.add(and);
        }

        if (planetsMin >= 0) {
            attributes.add("planets");
            compOps.add(">=");
            values.add(planetsMin);
            logicalOps.add(and);
        }

        if (planetsMax >= 0) {
            attributes.add("planets");
            compOps.add("<=");
            values.add(planetsMax);
            logicalOps.add(and);
        }

        if (goldilocksMin >= 0) {
            attributes.add("golds");
            compOps.add(">=");
            values.add(goldilocksMin);
            logicalOps.add(and);
        }

        if (goldilocksMax >= 0) {
            attributes.add("golds");
            compOps.add("<=");
            values.add(goldilocksMax);
            logicalOps.add(and);
        }

        if (distanceMin >= 0) {
            attributes.add("distance");
            compOps.add(">=");
            values.add(distanceMin);
            logicalOps.add(and);
        }


        if (distanceMax >= 0) {
            attributes.add("distance");
            compOps.add("<=");
            values.add(distanceMax);
            logicalOps.add(and);
        }


        allSys = QueriesWithDBConnection.getStarsFilterByMultipleAttr(attributes, compOps, values, logicalOps);


        return allSys;
    }

    public static void close() {
        try {
            connect.close();
        } catch (Exception e) {

        }
        sshSession.disconnect();
    }

    private static void doSshTunnel(String strSshUser, String strSshPassword, String strSshHost, int nSshPort,
                                    String strRemoteHost, int nLocalPort, int nRemotePort) throws JSchException {
        final JSch jsch = new JSch();
        Session session = jsch.getSession(strSshUser, strSshHost, 22);
        session.setPassword(strSshPassword);

        final Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("useSSL", "false");
        session.setConfig(config);

        session.connect();
        session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
        sshSession = session;
    }

    private static Connection newConnect() {

        Connection c = null;

        try {
            if (useArgs) {
                // SSH server
                int nSshPort = 22; // remote SSH host port number
                String strRemoteHost = "ambari-head.csc.calpoly.edu"; // hostname or
                // ip of
                // your
                // database
                // server
                int nLocalPort = 3366; // local port number use to bind SSH tunnel
                int nRemotePort = 3306; // remote port number of your database
                String strDbUser = "stargazers"; // database loging username
                String strDbPassword = "stars_3"; // database login password

                if (connect == null)
                    doSshTunnel(sshUser, sshPW, sshHost, nSshPort, strRemoteHost, nLocalPort,
                        nRemotePort);

                //Class.forName("com.mysql.jdbc.Driver"); //input driver class name

                //input database url, user and password
                c = DriverManager.getConnection("jdbc:mysql://localhost:" + nLocalPort +
                                "/stargazers?useSSL=false&zeroDateTimeBehavior=CONVERT_TO_NULL&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        strDbUser,
                        strDbPassword);
            } else {
                c = DriverManager.getConnection("jdbc:mysql://ambari-head.csc.calpoly.edu:3306" +
                                "/stargazers?useSSL=false&zeroDateTimeBehavior=CONVERT_TO_NULL&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "stargazers", "stars_3");
            }

            return c;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {

        //call connect  to connect from off campus
        boolean ssh = true;
        connect(args[0], args[1], args[2]); //host name, ssh username, ssh password

        try {
            
            /*Class.forName("com.mysql.jdbc.Driver"); //input driver class name
            //input database url, user and password
            connect = DriverManager.getConnection("jdbc:mysql://ambari-head.csc.calpoly.edu:3306?zeroDateTimeBehavior=convertToNull", 
                    "stargazers", "stars_3");*/
            
            ArrayList<String> attributes = new ArrayList<String>();
            ArrayList<String> compOps = new ArrayList<String>();
            ArrayList<Object> values = new ArrayList<Object>();
            ArrayList<String> logicalOps = new ArrayList<String>();
            
            attributes.add("starMass");
            compOps.add("=");
            values.add(1.08);

            ArrayList<SolarSystem> s1 = QueriesWithDBConnection.getStarsFilterByMultipleAttr(attributes, compOps, values, logicalOps);
            
            System.out.println("\nSolarSystem 1:");
            
            for(SolarSystem sys: s1) {
                Star s = sys.getStar();
                ArrayList<Planet> planets = sys.getPlanets();
                System.out.println("starName: " + s.getStarName());
                
                for(Planet p: planets) {
                    System.out.println("     planetMass: " + p.getMass());
                }
            }

            attributes = new ArrayList<String>();
            compOps = new ArrayList<String>();
            values = new ArrayList<Object>();
            logicalOps = new ArrayList<String>();
            
            attributes.add("goldilocksInner"); //0
            attributes.add("goldilocksOuter");//1
            attributes.add("type");
            
            compOps.add(">="); //0
            compOps.add("<=");//1
            compOps.add("=");
            
            values.add(6.00);//0
            values.add(20.00);//1
            values.add("supergiant");
            
            logicalOps.add("and"); //0 and 1
            logicalOps.add("and");

            ArrayList<SolarSystem> s2 = QueriesWithDBConnection.getStarsFilterByMultipleAttr(attributes, compOps, values, logicalOps);
            
            System.out.println("\nSolarSystem 2:");
            
            for(SolarSystem sys: s2) {
                Star s = sys.getStar();
                ArrayList<Planet> planets = sys.getPlanets();
                System.out.println("starName: " + s.getStarName());
                
                for(Planet p: planets) {
                    System.out.println("     planetMass: " + p.getMass());
                }
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        


    }

}
