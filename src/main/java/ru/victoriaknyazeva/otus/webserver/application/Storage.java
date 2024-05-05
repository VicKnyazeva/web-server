package ru.victoriaknyazeva.otus.webserver.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Storage {
    private static final Logger logger = LogManager.getLogger(Storage.class.getName());
    private static List<Item> items;

    public static void init() {
        logger.info("Хранилище проинициализировано");
        items = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            items.add(new Item("item " + i, 100 + (int)(Math.random() * 1000)));
        }
    }

    public static List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static void save(Item item) {
        item.setId(UUID.randomUUID());
        items.add(item);
    }

    public static Item getById(UUID id) {
        for (var item : items) {
            if (item.getId().compareTo(id) == 0) {
                return item;
            }
        }
        return null;
    }

    public static boolean update(Item newItem) {
        Item item = getById(newItem.getId());
        if (item == null) {
            return false;
        }
        item.setTitle(newItem.getTitle());
        item.setPrice(newItem.getPrice());
        return true;
    }
}
