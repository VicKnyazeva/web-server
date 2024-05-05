package ru.victoriaknyazeva.otus.webserver.application.processors;

import com.google.gson.Gson;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.application.Item;
import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetAllProductsProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        List<Item> items = Storage.getItems();
        Gson gson = new Gson();
        String result = "HTTP/1.1 200 OK\r\nContent-Type: application/json; charset=utf-8\r\n\r\n" + gson.toJson(items);
        output.write(result.getBytes(StandardCharsets.UTF_8));
    }
}
