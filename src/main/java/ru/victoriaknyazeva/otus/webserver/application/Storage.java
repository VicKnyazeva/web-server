package ru.victoriaknyazeva.otus.webserver.application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Storage implements StorageService {
    private final DbClient dbClient;

    public Storage() {
        dbClient = new DbClient();
    }

    public List<Item> getItems() throws StorageException {
        final String QUERY = "select id, title, price from items";

        List<Item> result = new ArrayList<>();
        try {
            try (PreparedStatement ps = dbClient.connection.prepareStatement(QUERY)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Item item = new Item();
                        item.setId(rs.getInt("id"));
                        item.setTitle(rs.getString("title"));
                        item.setPrice(rs.getInt("price"));
                        result.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Storage.getItems", e);
        }
        return Collections.unmodifiableList(result);
    }

    public void create(Item item) throws StorageException {
        final String INSERT_QUERY = "INSERT INTO items (title, price) VALUES(?,?) RETURNING id;";
        try (PreparedStatement ps = dbClient.connection.prepareStatement(INSERT_QUERY)) {
            ps.setString(1, item.getTitle());
            ps.setInt(2, item.getPrice());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Storage.create", e);
        }
    }

    public Item getById(int id) throws StorageException {
        final String QUERY = "select id, title, price from items where id=?";

        try {
            try (PreparedStatement ps = dbClient.connection.prepareStatement(QUERY)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Item item = new Item();
                        item.setId(rs.getInt("id"));
                        item.setTitle(rs.getString("title"));
                        item.setPrice(rs.getInt("price"));
                        return item;
                    }
                }
                return null; // ничего не нашли
            }
        } catch (SQLException e) {
            throw new StorageException("Storage.getById", e);
        }
    }

    public boolean update(Item newItem) throws StorageException {
        final String QUERY = "UPDATE items SET title=?, price=? WHERE id=?";
        try {
            try (PreparedStatement ps = dbClient.connection.prepareStatement(QUERY)) {
                ps.setString(1, newItem.getTitle());
                ps.setInt(2, newItem.getPrice());
                ps.setInt(3, newItem.getId());
                int updated = ps.executeUpdate();
                return updated == 1;
            }
        } catch (SQLException e) {
            throw new StorageException("Storage.update", e);
        }
    }

    public boolean delete(int itemId) throws StorageException {
        final String QUERY = "DELETE FROM items WHERE id=?";
        try {
            try (PreparedStatement ps = dbClient.connection.prepareStatement(QUERY)) {
                ps.setInt(1, itemId);
                int updated = ps.executeUpdate();
                return updated == 1;
            }
        } catch (SQLException e) {
            throw new StorageException("Storage.getById", e);
        }
    }
}
