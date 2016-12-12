/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.planrnaseq;

import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

/**
 *
 * @author pak2013
 */
public class ConnectionPool {

    private static SimpleJDBCConnectionPool connectionPool = null;

    public synchronized static SimpleJDBCConnectionPool getConnectionPool() {
        try {
            if (connectionPool == null) {
            // Very very important: Currently wait_timeout at mysql server is increased. Ideally, it 
                // can be solved using validation query (http://stackoverflow.com/questions/12747969/unable-to-connect-to-mysql-in-cloudbees-communicationsexception-communications)
                // I will try this in future
                // System.out.println(" Creating  connection pool to Mysql RNA database");
                connectionPool = new SimpleJDBCConnectionPool(
                        "com.mysql.jdbc.Driver",
                       "jdbc:mysql://localhost:3306/rna", "rnaseq",
                        "rna", 2, 100);
                //   ?autoReconnect=true&autoReconnectForPools=true
            // System.out.println("Connection pool created");                

            }
        
            
        } catch (Exception e) {
            // System.out.println("problem in creating connection pool");
            e.printStackTrace();
        }
        return connectionPool;
    }

}
