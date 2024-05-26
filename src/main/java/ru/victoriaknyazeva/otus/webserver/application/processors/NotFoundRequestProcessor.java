package ru.victoriaknyazeva.otus.webserver.application.processors;

import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;
import ru.victoriaknyazeva.otus.webserver.NotFoundException;

public class NotFoundRequestProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        throw new NotFoundException("Страница отсутствует");
    }
}