package com.econest.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Single shared connection to the SQLite database file (econest.db in the project folder).
// The constructor is private so nobody can create objects of this class,
// everyone just calls DBConnection.getConnection().
public class DBConnection {
    private static final String URL = "jdbc:sqlite:econest.db";
    private static Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        // only open a new connection the first time (or if it was closed)
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            // try-with-resources: the Statement is closed automatically at the end
            try (Statement st = connection.createStatement()) {
                // SQLite ignores foreign keys unless this is switched on
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }
}
