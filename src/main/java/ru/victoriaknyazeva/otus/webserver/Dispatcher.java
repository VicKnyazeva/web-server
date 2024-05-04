package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.processors.CalculatorRequestProcessor;
import ru.victoriaknyazeva.otus.webserver.processors.HelloWorldRequestProcessor;
import ru.victoriaknyazeva.otus.webserver.processors.NotFoundRequestProcessor;
import ru.victoriaknyazeva.otus.webserver.processors.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> router;
    private RequestProcessor unknownOperationRequestProcessor;

    public Dispatcher() {
        this.router = new HashMap<>();
        this.router.put("/calc", new CalculatorRequestProcessor());
        this.router.put("/hello", new HelloWorldRequestProcessor());
        this.unknownOperationRequestProcessor = new NotFoundRequestProcessor();
    }

    public void execute(HttpRequest httpRequest, OutputStream outputStream) throws InterruptedException, IOException {
        if (!router.containsKey(httpRequest.getUri())) {
            unknownOperationRequestProcessor.execute(httpRequest, outputStream);
            return;
        }
        var processor = router.get(httpRequest.getUri());
        processor.execute(httpRequest, outputStream);
    }
}
