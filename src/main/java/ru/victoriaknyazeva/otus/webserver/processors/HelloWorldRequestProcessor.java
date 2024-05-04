package ru.victoriaknyazeva.otus.webserver.processors;

import ru.victoriaknyazeva.otus.webserver.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloWorldRequestProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws InterruptedException, IOException {
        Thread.sleep(10000);
        String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\n\r\n<html><body><h1>Hello World!!!</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
