package ru.victoriaknyazeva.otus.webserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.application.DbClient;
import ru.victoriaknyazeva.otus.webserver.application.processors.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        int port = Integer.parseInt((String) System.getProperties().getOrDefault("port", "8189"));
        createDatabaseClient();
        try {
            HttpServer server = new HttpServer(port);
            registerApplicationRoutes(server);
            server.start();
        } catch (Exception e) {
            logger.error("Не удалось создать или сконфигурировать сервер", e);
            System.exit(1);
        }
    }

    private static void createDatabaseClient() {
        try {
            DbClient dbClient = new DbClient();
            dbClient.prepareDatabase();
            logger.debug("Подключение к базе данных проинициализировано");
        } catch (Exception e) {
            logger.error("Не удалось проинициализировать подключение к базе данных", e);
            System.exit(1);
        }
    }

    private static void registerApplicationRoutes(HttpServer server) {
        server.registerRoute(HttpMethod.GET, "/items/{id}", new GetProductByIdProcessor());
        server.registerRoute(HttpMethod.GET, "/items", new GetAllProductsProcessor());
        server.registerRoute(HttpMethod.POST, "/items", new CreateNewProductProcessor());
        server.registerRoute(HttpMethod.PUT, "/items/{id}", new UpdateProductProcessor());
        server.registerRoute(HttpMethod.DELETE, "/items/{id}", new DeleteProductProcessor());
        server.registerRoute(HttpMethod.POST, "/stop", new StopRequestProcessor());
    }
}