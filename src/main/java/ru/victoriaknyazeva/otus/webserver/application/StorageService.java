package ru.victoriaknyazeva.otus.webserver.application;

import java.util.List;

public interface StorageService {
    List<Item> getItems() throws StorageException;

    Item getById(int id) throws StorageException;

    boolean update(Item newItem) throws StorageException;

    boolean delete(int id) throws StorageException;

    void create(Item item) throws StorageException;
}
