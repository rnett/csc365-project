package exoplanetsolarsystemviewer; //comment out later when whole program gets integrated

import java.sql.*;
import java.util.ArrayList;
        
public class QueriesWithDBConnection {

    static Connection connect;
    
    /*query from stars table and filter by condition for an attribute
    for values, you must pass in the appropriate type depending on the column
    
    operators supported: =, >=, <=
    
    user for sql statements like:
    select * from stargazers.stars where ___
    */
    public static ResultSet getStarsFilterBySingleAttr(String attribute, String compareOp, Object value) throws Exception {
        
        ResultSet rs = null;
        
        try {
            Statement statement = connect.createStatement();
            
            String sqlStatement = "select * from stargazers.stars";
            
            if(attribute == null || compareOp == null) {
                return rs;
            }
            
            
            if(compareOp != "<=" && compareOp != "=" && compareOp != ">=") {
                System.out.println("Unsupported operator.");
                return rs;
            }
            
            if(value instanceof String || value instanceof Double || value instanceof Integer) {
                sqlStatement += " where " + attribute + " " + compareOp + " " + value;
            } else {
                System.out.println("Invalid value");
                return rs;
            }
           
            rs = statement.executeQuery(sqlStatement); //ResultSet is an iterator
            
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return rs;
    }
    
    /*
    
    underlying goal: Act as a filter for goldilocks or queries involving multiple attributes.
    
    supported logical operators: and, or
    */
    public static ResultSet getStarsFilterByMultipleAttr(ArrayList<String> attributes, 
            ArrayList<String> compareOps, ArrayList values, ArrayList<String> chainOps) throws Exception {
        
        ResultSet rs = null;
        
        try {
            Statement statement = connect.createStatement();
            
            String sqlStatement = "select * from stargazers.stars";
            
            if(attributes.isEmpty() || compareOps.isEmpty() || 
                    (   ( attributes.size() != compareOps.size() ) && ( compareOps.size() != values.size() )  )  || 
                    chainOps.size() != attributes.size() - 1) {
                return rs; 
            }
            
            if(attributes.size() > 0) {
                sqlStatement += " where ";
            }
            
            for(int i = 0; i < attributes.size(); i++) {
                
                if( compareOps.get(i) != "<=" && compareOps.get(i) != "=" &&  compareOps.get(i) != ">=" ) {
                    System.out.println("Unsupported operator.");
                    rs = null;
                    return rs;
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
                    return rs;
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
        
        return rs;
    }
    
    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver"); //input driver class name
            
            //input database url, user and password
            connect = DriverManager.getConnection("jdbc:mysql://ambari-head.csc.calpoly.edu:3306?zeroDateTimeBehavior=convertToNull", 
                    "stargazers", "stars_3");
            
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
        }
        
        
        
    }
    
}
