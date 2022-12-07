package com.example.demo.database;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DbConnection {
    private static Connection connection;

    public DbConnection() {
        final String url = "jdbc:postgresql://localhost:5432/energy";
        final String user = "root";
        final String password = "root";
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the energy database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection(){
        return connection;
    }
}
