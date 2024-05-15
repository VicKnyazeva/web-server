package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;
import ru.victoriaknyazeva.otus.webserver.NotFoundException;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;
import ru.victoriaknyazeva.otus.webserver.application.StorageService;

import java.nio.charset.StandardCharsets;

public class GetProductByIdProcessor implements RequestProcessor {
    private final StorageService storage;

    public GetProductByIdProcessor() {
        this.storage = new Storage();
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        int id = Integer.parseInt(httpRequest.getRouteParameter("id"));
        Item item = storage.getById(id);
        if (item == null) {
            throw new NotFoundException("Не найдено");
        } else {
            Gson gson = new Gson();
            response.body.write(gson.toJson(item).getBytes(StandardCharsets.UTF_8));
        }
    }
}
