package ru.victoriaknyazeva.otus.webserver.application.processors;

import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;

public interface RequestProcessor {
    void execute(HttpRequest httpRequest, HttpResponse response) throws Exception;
}
