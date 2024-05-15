package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.application.processors.NotFoundRequestProcessor;
import ru.victoriaknyazeva.otus.webserver.application.processors.RequestProcessor;
import ru.victoriaknyazeva.otus.webserver.routing.Route;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
    private final List<Route> routes;
    private RequestProcessor unknownOperationRequestProcessor;

    public Dispatcher() {
        this.routes = new ArrayList<>();
        this.unknownOperationRequestProcessor = new NotFoundRequestProcessor();
    }

    public void registerRoute(HttpMethod method, String routePathRegex, RequestProcessor processor) {
        Route r = new Route(method, routePathRegex, processor);
        this.routes.add(r);
    }

    public void execute(HttpRequest httpRequest, HttpResponse response) throws Exception {
        for (Route r : routes) {
            if (r.match(httpRequest)) {
                r.processor.execute(httpRequest, response);
                return;
            }
        }
        unknownOperationRequestProcessor.execute(httpRequest, response);
    }
}
