package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.BadRequestException;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;
import ru.victoriaknyazeva.otus.webserver.NotFoundException;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;
import ru.victoriaknyazeva.otus.webserver.application.StorageService;

public class UpdateProductProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(UpdateProductProcessor.class.getName());

    private final StorageService storage;

    public UpdateProductProcessor() {
        this.storage = new Storage();
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        try {
            Gson gson = new Gson();
            Item newItem = gson.fromJson(httpRequest.getBody(), Item.class);
            newItem.setId(Integer.parseInt(httpRequest.getRouteParameter("id")));
            if (storage.update(newItem)) {
                logger.info("{} -> {}", httpRequest.getUri(), response.getStatusText());
            } else {
                throw new NotFoundException("Не найдено");
            }
        } catch (JsonParseException e) {
            throw new BadRequestException("", e);
        }
    }
}
