package ru.victoriaknyazeva.otus.webserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.application.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class HttpServer {
    private static final Logger logger = LogManager.getLogger(HttpServer.class.getName());

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

            String msg = "Сервер запущен на порту: " + port;
            logger.info(msg);
            System.out.println(msg);

            this.dispatcher = new Dispatcher();
            logger.info("Диспетчер проинициализирован");

            while (!serverSocket.isClosed()) {
                Socket socket = accept();
                if (socket != null)
                    enqueueTaskForRequest(socket);
            }
        } catch (IOException e) {
            logger.fatal("Исключение", e);
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
                logger.error("stop: ", e);
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
                    logger.info("Start request ({}) processing", request.getUri());
                    try {
                        dispatcher.execute(request, out);
                    } finally {
                        logger.info("End request ({}) processing", request.getUri());
                    }
                }
            } catch (InterruptedException e) {
                if (serverStopInProgress)
                    return;
                logger.error("enqueueTaskForRequest: ", e);
            } catch (IOException e) {
                logger.error("enqueueTaskForRequest: ", e);
            }
        });
    }

    private static HttpRequest parseRequest(InputStream in) throws IOException {
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
