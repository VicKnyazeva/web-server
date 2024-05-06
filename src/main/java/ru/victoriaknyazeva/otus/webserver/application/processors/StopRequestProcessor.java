package ru.victoriaknyazeva.otus.webserver.application.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;

public class StopRequestProcessor implements RequestProcessor {

    private static final Logger logger = LogManager.getLogger(StopRequestProcessor.class.getName());

    @Override
    public void execute(HttpRequest httpRequest, OutputStream output) throws IOException {
        String msg = "Получен запрос на остановку сервера";
        logger.info(msg);
        System.out.println("Получен запрос на остановку сервера");
        System.exit(0);
    }
}
