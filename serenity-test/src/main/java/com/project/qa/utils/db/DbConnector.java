package com.project.qa.utils.db;

import com.project.qa.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {

    static PropertiesLoader propertiesLoader = new PropertiesLoader();
    private Connection connectionReadNaturally = null;
    private Connection connectionReadNaturallyReport = null;
    private static Logger LOGGER = LoggerFactory.getLogger(DbExecutor.class);
    private static Object monitor = new Object();
    private static final String USER = propertiesLoader.getProperty("db.login");
    private static final String PASS = propertiesLoader.getProperty("db.pass");

    public DbConnector() {
    }

    public DbExecutor to(DbExecutor.DB dbToConnect) {
        synchronized (monitor) {
            Connection currentConnection = null;
            switch (dbToConnect) {
                case READ_NATURALLY:
                    if (isConnectionAlive(connectionReadNaturally)) {
                        currentConnection = connectionReadNaturally;
                    } else {
                        currentConnection = createConnection(dbToConnect);
                        connectionReadNaturally = currentConnection;
                    }
                    break;
                case READ_NATURALLY_REPORT:
                    if (isConnectionAlive(connectionReadNaturallyReport)) {
                        currentConnection = connectionReadNaturallyReport;
                    } else {
                        currentConnection = createConnection(dbToConnect);
                        connectionReadNaturallyReport = currentConnection;
                    }
                        break;
            }
            return DbExecutor.getDbExecutor(currentConnection, this);
        }
    }

    private boolean isConnectionAlive(Connection connection) {
        boolean result = false;
        try {
            result = (connection == null || connection.isClosed()) ? false : true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Connection createConnection(DbExecutor.DB dbType) {
        Connection connection = null;
        try {
            String dbUrl = propertiesLoader.getProperty(dbType.getPropertyName());
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, USER, PASS);
            if (connection != null) {
                LOGGER.info("Connected database successfully...");
            } else throw new RuntimeException("Connection to database is failed!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver is not found!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
