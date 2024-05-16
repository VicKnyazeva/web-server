package ru.victoriaknyazeva.otus.webserver.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Storage {
    private static final Logger logger = LogManager.getLogger(Storage.class.getName());
    private static final List<Item> items = new ArrayList<>();

    public synchronized static void init() {
        logger.debug("Хранилище проинициализировано");

        for (int i = 0; i < 3; i++) {
            items.add(new Item("item " + i, 100 + (int) (Math.random() * 1000)));
        }
    }

    public synchronized static List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static void save(Item item) {
        items.add(item);
    }

    public synchronized static Item getById(UUID id) {
        return items
                .stream()
                .filter((item) -> item.id() == id)
                .findFirst()
                .orElse(null);
    }

    public synchronized static boolean update(Item newItem) {
        var ind = items.indexOf(newItem);
        if (ind < 0) {
            return false;
        }
        items.set(ind, newItem);
        return true;
    }
}
