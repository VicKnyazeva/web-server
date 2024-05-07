package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UpdateProductProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(UpdateProductProcessor.class.getName());

    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        int status;
        String statusMessage;
        String message = null;
        try {
            Gson gson = new Gson();
            Item newItem = gson.fromJson(httpRequest.getBody(), Item.class);
            if (Storage.update(newItem)) {
                status = 200;
                statusMessage = "OK";
                logger.info("{} -> {}", httpRequest.getUri(), statusMessage);
            } else {
                status = 404;
                statusMessage = "Not Found";
                logger.info("{} -> {}", httpRequest.getUri(), statusMessage);
            }
        } catch (JsonParseException e) {
            status = 400;
            statusMessage = "Bad Request";
            message = e.getMessage();
            logger.error("{} -> {}", httpRequest.getUri(), e);
        } catch (Exception e) {
            status = 500;
            statusMessage = "Internal Server Error";
            message = e.getMessage();
            logger.error(String.format("%s -> %s", httpRequest.getUri(), statusMessage), e);
        }
        if (message == null) {
            message = statusMessage;
        }
        String response = String.format("HTTP/1.1 %d %s\r\nContent-Type: text/plain; charset=utf-8\r\n\r\n%d. %s",
                status, statusMessage,
                status, message);
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
