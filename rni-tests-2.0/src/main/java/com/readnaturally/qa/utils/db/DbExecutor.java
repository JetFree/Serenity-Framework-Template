package com.readnaturally.qa.utils.db;

import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;
import com.readnaturally.qa.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by Evgeny.Gurinovich on 29.04.2016.
 */
public class DbExecutor {

    private static DbExecutor dbExecutor;
    private static Statement stmt = null;
    private static Connection connection;
    private static Logger LOGGER = LoggerFactory.getLogger(DbExecutor.class);
    private static Object monitor = new Object();
    private static DbConnector dbConnector;

    private DbExecutor(Connection connection, DbConnector dbConnector) {
        this.connection = connection;
        this.dbConnector = dbConnector;
    }

    public static DbExecutor getDbExecutor(Connection connection, DbConnector dbConnector) {
        if (dbExecutor == null) {
            dbExecutor = new DbExecutor(connection, dbConnector);
        } else {
            dbExecutor.setConnection(connection);
        }
        return dbExecutor;
    }

    public static DbConnector connect() {
        return new DbConnector();
    }


    public Connection getConnection() {
        return this.connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ResultSet getResultSet(String sql) {
        LOGGER.info(sql);
        try {
            stmt = connection.createStatement();
            ResultSet resultSet;
            resultSet = stmt.executeQuery(sql);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void executeQuery(String query) {
        LOGGER.info(query);
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
        } catch (MySQLNonTransientConnectionException e) {
            LOGGER.info("Have gotten MySQLNonTransientConnectionException exception. Trying to reconnect to db.");
//            closeConnection();
//            connectDb();
//            try {
//                stmt = connection.createStatement();
//                stmt.executeUpdate(query);
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public  Map<String, List<String>> getValuesFromResultSet(String query, String... columns) {
        ResultSet dbResultSet = getResultSet(query);
        Map<String, List<String>> resultMap = Collections.synchronizedMap(new HashMap<>());
        synchronized (resultMap) {
            try {
                if (dbResultSet.next()) {
                    dbResultSet.beforeFirst();
                    List<String> resultListForColumn;
                    for (String column : columns) {
                        resultListForColumn = new ArrayList<>();
                        while (dbResultSet.next()) {
                            resultListForColumn.add(dbResultSet.getString(column));
                        }
                        resultMap.put(column, resultListForColumn);
                        dbResultSet.beforeFirst();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }

    public void closeConnection() {
        synchronized (monitor) {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                        LOGGER.info("Db connection closed.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public enum DB {
        READ_NATURALLY("db.qa.url"),
        READ_NATURALLY_REPORT("db.qa.report.url");

        private String propertyName;

        DB(String value) {
            this.propertyName = value;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }
}
