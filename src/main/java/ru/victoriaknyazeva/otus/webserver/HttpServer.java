package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.application.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = LogManager.getLogger(HttpServer.class.getName());

    private final int port;
    private final ExecutorService executorService;

    public HttpServer(int port) {
        this.port = port;
        this.executorService = Executors.newCachedThreadPool(); // сервис-исполнитель обработчиков запросов
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            String msg = "Сервер запущен на порту: " + port;
            logger.info(msg);
            System.out.println(msg);

            Dispatcher dispatcher = new Dispatcher();
            logger.debug("Диспетчер проинициализирован");
            Storage.init();
            while (true) {
                Socket socket = serverSocket.accept();
                enqueueTaskForRequest(socket, dispatcher);
            }
        } catch (IOException e) {
            logger.fatal("Исключение", e);
        }
    }

    private void enqueueTaskForRequest(Socket socket, Dispatcher dispatcher) {
        executorService.execute(() -> {
            try (Socket ignored = socket;
                 OutputStream out = socket.getOutputStream();
                 InputStream in = socket.getInputStream()) {
                HttpRequest request = getRequest(in);
                if (request != null) {
                    logger.info("Start request ({}) processing", request.getUri());
                    try {
                        dispatcher.execute(request, out);
                    } finally {
                        logger.info("End request ({}) processing", request.getUri());
                    }
                }
            } catch (IOException e) {
                logger.error("enqueueTaskForRequest: ", e);
            }
        });
    }

    private HttpRequest getRequest(InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        int n = in.read(buffer);
        if (n > 0) {
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            logger.debug(request.info(true));
            return request;
        }
        return null;
    }
}
