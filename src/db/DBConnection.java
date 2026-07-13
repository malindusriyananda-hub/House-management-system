package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "wren_house_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null || instance.getConnection() == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
