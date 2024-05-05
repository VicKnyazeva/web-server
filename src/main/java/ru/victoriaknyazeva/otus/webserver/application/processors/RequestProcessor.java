package ru.victoriaknyazeva.otus.webserver.application.processors;

import ru.victoriaknyazeva.otus.webserver.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestProcessor {
    void execute(HttpRequest httpRequest, OutputStream output) throws IOException;
}
