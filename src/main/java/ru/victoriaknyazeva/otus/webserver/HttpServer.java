package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            this.dispatcher = new Dispatcher();
            System.out.println("Диспетчер проинициализирован");
            Storage.init();
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    byte[] buffer = new byte[8192];
                    int n = socket.getInputStream().read(buffer);
                    if (n > 0) {
                        String rawRequest = new String(buffer, 0, n);
                        HttpRequest request = new HttpRequest(rawRequest);
                        request.info(true);

                        dispatcher.execute(request, socket.getOutputStream());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
