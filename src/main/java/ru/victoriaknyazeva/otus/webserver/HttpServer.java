package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final int port;
    private final ExecutorService executorService;

    public HttpServer(int port) {
        this.port = port;
        this.executorService = Executors.newCachedThreadPool(); // сервис-исполнитель обработчиков запросов
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            Dispatcher dispatcher = new Dispatcher();
            System.out.println("Диспетчер проинициализирован");
            Storage.init();
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> {
                    try (Socket ignored = socket;
                         OutputStream out = socket.getOutputStream();
                         InputStream in = socket.getInputStream()) {
                        HttpRequest request = getRequest(in);
                        if (request != null) {
                            dispatcher.execute(request, out);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpRequest getRequest(InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        int n = in.read(buffer);
        if (n > 0) {
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.info(true);
            return request;
        }
        return null;
    }
}
