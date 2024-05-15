package ru.victoriaknyazeva.otus.webserver.application.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.HttpResponse;

public class StopRequestProcessor implements RequestProcessor {

    private static final Logger logger = LogManager.getLogger(StopRequestProcessor.class.getName());

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        String msg = "Получен запрос на остановку сервера";
        logger.info(msg);
        System.out.println("Получен запрос на остановку сервера");
        System.exit(0);
    }
}
