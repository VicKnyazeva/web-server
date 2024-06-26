package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;
import ru.victoriaknyazeva.otus.webserver.application.StorageService;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetAllProductsProcessor implements RequestProcessor {
    private final StorageService storage;

    public GetAllProductsProcessor() {
        this.storage = new Storage();
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        List<Item> items = storage.getItems();
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.body.write(gson.toJson(items).getBytes(StandardCharsets.UTF_8));
    }
}
