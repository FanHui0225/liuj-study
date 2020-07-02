package com.stereo.study.hive;

import java.sql.*;

/**
 * Created by liuj-ai on 2020/7/2.
 */
public class HiveJDBCExample {

    private static String connectionString = "jdbc:hive2://hdp5.glodon.com:10000/mytest";
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String queryString = "SELECT table_revision.sys_creator_id, count(DISTINCT table_revision.project_id), count(DISTINCT table_revision.table_name) FROM table_revision GROUP BY table_revision.sys_creator_id;";
    private static Connection con;
    private static ResultSet resultSet;
    private static Statement sqlStatement;

    public static void main(String[] args) {
        System.out.println("Loaded the driver successfully. Trying to establish connection");
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(connectionString);
            System.out.println("Created connection. Preparing statement");
            sqlStatement = con.createStatement();
            System.out.println("Executing " + queryString);
            resultSet = sqlStatement.executeQuery(queryString);
            while (resultSet.next()) {
                System.out.println("Result set " + resultSet.getString(1));
            }
            con.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
