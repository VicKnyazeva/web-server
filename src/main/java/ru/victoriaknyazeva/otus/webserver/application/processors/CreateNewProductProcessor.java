package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateNewProductProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        Gson gson = new Gson();
        Item item = gson.fromJson(httpRequest.getBody(), Item.class);
        Storage.save(item);
        String jsonOutItem = gson.toJson(item);

        String response = "HTTP/1.1 200 OK\r\nContent-Type: application/json; charset=utf-8\r\n\r\n" + jsonOutItem;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
