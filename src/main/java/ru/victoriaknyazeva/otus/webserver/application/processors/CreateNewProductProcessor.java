package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.BadRequestException;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;
import ru.victoriaknyazeva.otus.webserver.application.StorageService;

import java.nio.charset.StandardCharsets;

public class CreateNewProductProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(CreateNewProductProcessor.class.getName());

    private final StorageService storage;

    public CreateNewProductProcessor() {
        this.storage = new Storage();
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        Item item;
        Gson gson = new Gson();
        try {
            item = gson.fromJson(httpRequest.getBody(), Item.class);
        } catch (JsonParseException e) {
            throw new BadRequestException("", e);
        }
        storage.create(item);
        String jsonOutItem = gson.toJson(item);

        response.setContentType("application/json");
        response.body.write(jsonOutItem.getBytes(StandardCharsets.UTF_8));
    }
}
