package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.BadRequestException;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;
import ru.victoriaknyazeva.otus.webserver.NotFoundException;
import ru.victoriaknyazeva.otus.webserver.application.Storage;
import ru.victoriaknyazeva.otus.webserver.application.StorageService;

public class DeleteProductProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(DeleteProductProcessor.class.getName());

    private final StorageService storage;

    public DeleteProductProcessor() {
        this.storage = new Storage();
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        try {
            int itemId = Integer.parseInt(httpRequest.getRouteParameter("id"));
            if (storage.delete(itemId)) {
                logger.info("{} -> {}", httpRequest.getUri(), response.getStatusText());
            } else {
                throw new NotFoundException("Не найдено");
            }
        } catch (JsonParseException e) {
            throw new BadRequestException("", e);
        }
    }
}
