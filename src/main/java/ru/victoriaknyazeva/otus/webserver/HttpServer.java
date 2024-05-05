package ru.victoriaknyazeva.otus.webserver;

import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class HttpServer {
    private final int port;
    private final ExecutorService executorService;

    private Thread workThread;
    private ServerSocket serverSocket;
    private boolean serverStopInProgress;
    private Dispatcher dispatcher;

    public HttpServer(int port, ExecutorService executorService) {
        this.port = port;
        this.executorService = executorService;
    }

    /**
     * создаёт и запускает рабочий поток сервера
     */
    public void start() {
        workThread = new Thread(() -> {
            run();
        });
        workThread.start();
    }

    /**
     * ждёт остановки рабочего потока сервера
     */
    public void waitForStop() {
        try {
            workThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void run() {
        try {
            Storage.init();
            this.serverSocket = new ServerSocket(port);

            System.out.println("Сервер запущен на порту: " + port);
            this.dispatcher = new Dispatcher();
            System.out.println("Диспетчер проинициализирован");

            while (!serverSocket.isClosed()) {
                Socket socket = accept();
                if (socket != null)
                    enqueueTaskForRequest(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverStopInProgress = false;
        }
    }

    /**
     * инициирует остановку рабочего потока сервера
     */
    public synchronized void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverStopInProgress = true;
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Socket accept() throws IOException {
        try {
            return serverSocket.accept();
        } catch (SocketException e) {
            if (serverStopInProgress)
                return null;
            else
                throw e;
        }
    }

    public void enqueueTaskForRequest(Socket socket) {
        executorService.execute(() -> {
            try (var ignored = socket;
                 var out = socket.getOutputStream();
                 var in = socket.getInputStream()) {
                HttpRequest request = parseRequest(in);
                if (request != null) {
                    dispatcher.execute(request, out);
                }
            } catch (InterruptedException e) {
                if (serverStopInProgress)
                    return;
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static HttpRequest parseRequest(InputStream in) throws IOException {
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
