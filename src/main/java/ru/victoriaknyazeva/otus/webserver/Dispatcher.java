package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.application.processors.*;
import ru.victoriaknyazeva.otus.webserver.application.processors.NotFoundRequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<String, RequestProcessor> router;
    private RequestProcessor unknownOperationRequestProcessor;

    public Dispatcher() {
        this.router = new HashMap<>();
        this.router.put("GET /calc", new CalculatorRequestProcessor());
        this.router.put("GET /hello", new HelloWorldRequestProcessor());
        this.router.put("GET /items", new GetAllProductsProcessor());
        this.router.put("POST /items", new CreateNewProductProcessor());
        this.router.put("PUT /items", new UpdateProductProcessor());
        this.unknownOperationRequestProcessor = new NotFoundRequestProcessor();
    }

    public void execute(HttpRequest httpRequest, OutputStream outputStream) throws InterruptedException, IOException {
        if (!router.containsKey(httpRequest.getRouteKey())) {
            unknownOperationRequestProcessor.execute(httpRequest, outputStream);
            return;
        }
        var processor = router.get(httpRequest.getRouteKey());
        processor.execute(httpRequest, outputStream);
    }
}
