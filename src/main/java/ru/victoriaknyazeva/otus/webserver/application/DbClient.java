package ru.victoriaknyazeva.otus.webserver.application;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class DbClient {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());
    private static final String dbUser = "dbuser";
    private static final String dbPswd = "dbuser";
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/storage";

    public final Connection connection;

    public DbClient() throws RuntimeException {
        try {
            this.connection = getConnection(DATABASE_URL, dbUser, dbPswd);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void prepareDatabase() throws SQLException {
        int totalItems = 0;
        try (PreparedStatement ps = connection.prepareStatement("SELECT count(*) from items;")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalItems = rs.getInt(1);
                }
            }
        }
        if (totalItems == 0) {
            for (int i = 0; i < 3; i++) {
                Item item = new Item("item " + i, 100 + (int) (Math.random() * 1000));
                this.insertItem(item);
            }
        }
    }

    private void insertItem(Item item) throws SQLException {
        final String INS_ITEM_QUERY = "INSERT INTO items (title, price) VALUES(?,?);";
        try (PreparedStatement ps = connection.prepareStatement(INS_ITEM_QUERY)) {
            ps.setString(1, item.getTitle());
            ps.setInt(2, item.getPrice());
            ps.executeUpdate();
        }
    }
}
