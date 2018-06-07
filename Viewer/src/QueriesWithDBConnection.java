//package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class QueriesWithDBConnection {

    private static Connection connect;

    private static Session sshSession = null;

    public static Connection connect(String[] args) {
        if (connect == null) {

            connect = newConnect(true, args[0], args[1], args[2]);

            //deployment version
            //connect = newConnect(false, null, null, null);
        }

        return connect;
    }

    public static Connection connect() {
        try {
            if (connect == null || connect.isClosed()) {

                connect = newConnect(false, null, null, null);
            }
        } catch (Exception e) {
            connect = newConnect(false, null, null, null);
        }

        return connect;
    }

    /*query from stars table and filter by condition for an attribute
    for values, you must pass in the appropriate type depending on the column
    
    operators supported: =, >=, <=
    
    user for sql statements like:
    select * from stargazers.stars where ___
    */
    public static SolarSystem getStarsFilterBySingleAttr(String attribute, String compareOp, Object value) throws Exception {

        ResultSet rs = null;

        try {
            Statement statement = connect().createStatement();

            String sqlStatement = "select * from stargazers.stars natural join stargazers.planets";

            if(attribute == null || compareOp == null) {
                return new SolarSystem(rs);
            }


            if(compareOp != "<=" && compareOp != "=" && compareOp != ">=") {
                System.out.println("Unsupported operator.");
                return new SolarSystem(rs);
            }

            if(value instanceof String || value instanceof Double || value instanceof Integer) {
                sqlStatement += " where " + attribute + " " + compareOp + " " + value;
            } else {
                System.out.println("Invalid value");
                return new SolarSystem(rs);
            }

            rs = statement.executeQuery(sqlStatement); //ResultSet is an iterator


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new SolarSystem(rs);
    }

    public static ArrayList<SolarSystem> getSystems(Star.Type type, int planetsMin, int planetsMax, int goldilocksMin, int goldilocksMax, int distanceMin, int distanceMax) {
        return new ArrayList<SolarSystem>();
    }

    /*
    
    underlying goal: Act as a filter for goldilocks or queries involving multiple attributes.
    
    supported logical operators: and, or
    */
    public static SolarSystem getStarsFilterByMultipleAttr(ArrayList<String> attributes,
                                                           ArrayList<String> compareOps,
                                                           ArrayList values, ArrayList<String> chainOps) throws Exception {

        ResultSet rs = null;

        try {
            Statement statement = connect().createStatement();

            String sqlStatement = "select * from stargazers.stars natural join stargazers.planets";

            if (attributes.isEmpty() || compareOps.isEmpty() ||
                    ((attributes.size() != compareOps.size()) && (compareOps.size() != values.size())) ||
                    chainOps.size() != attributes.size() - 1) {
                return new SolarSystem(rs);
            }

            if(attributes.size() > 0) {
                sqlStatement += " where ";
            }

            for(int i = 0; i < attributes.size(); i++) {

                if( compareOps.get(i) != "<=" && compareOps.get(i) != "=" &&  compareOps.get(i) != ">=" ) {
                    System.out.println("Unsupported operator.");
                    rs = null;
                    return new SolarSystem(rs);
                }

                if( (values.get(i) instanceof String || values.get(i) instanceof Double || values.get(i) instanceof Integer) ) {

                    if(!chainOps.isEmpty()) {
                        sqlStatement += attributes.get(i) + " " + compareOps.get(i) + " " + values.get(i) + " " + chainOps.remove(0) + " ";
                    } else {
                        sqlStatement += attributes.get(i) + " " + compareOps.get(i) + " " + values.get(i);
                    }

                } else {
                    System.out.println("Invalid value");
                    rs = null;
                    return new SolarSystem(rs);
                }


            }


            rs = statement.executeQuery(sqlStatement); //ResultSet is an iterator

            System.out.println("Query being executed: " + sqlStatement);

            //testing... delete later
            while(rs.next()) {
                String starName = rs.getString("starName");
                double goldilocksInner = rs.getDouble("goldilocksInner");
                double goldilocksOuter = rs.getDouble("goldilocksOuter");
                System.out.println("starName: " + starName + " " + "goldilocksInner: " + goldilocksInner + " goldilocksOuter: " + goldilocksOuter);

            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new SolarSystem(rs);
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

    private static Connection newConnect(boolean ssh, String sshHost, String sshUser, String sshPW) {

        Connection c = null;

        try {
            if (ssh) {
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

        boolean ssh = true;

        connect(args);

        try {
            String attribute = "starMass";
            String compOp = "=";
            double value = 1.08;

            QueriesWithDBConnection.getStarsFilterBySingleAttr(attribute, compOp, value);

            ArrayList<String> attributes = new ArrayList<String>();
            attributes.add("goldilocksInner");
            attributes.add("goldilocksOuter");
            ArrayList<String> compOps = new ArrayList<String>();
            compOps.add(">=");
            compOps.add("<=");
            ArrayList<Double> values = new ArrayList<Double>();
            values.add(6.00);
            values.add(20.00);
            ArrayList<String> logicalOps = new ArrayList<String>();
            logicalOps.add("and");

            QueriesWithDBConnection.getStarsFilterByMultipleAttr(attributes, compOps, values, logicalOps);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        //--------------------------------
        // This space is for putting your own driver code for development purposes.


        //--------------------------------


    }

}
