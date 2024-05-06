package ru.victoriaknyazeva.otus.webserver.application.processors;

import ru.victoriaknyazeva.otus.webserver.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UnknownOperationRequestProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        String response = "HTTP/1.1 404 Not found\r\nContent-Type: text/plane; charset=utf-8\r\n\r\n404. Страница отсутствует";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
