package ru.victoriaknyazeva.otus.webserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.victoriaknyazeva.otus.webserver.application.processors.RequestProcessor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = LogManager.getLogger(HttpServer.class.getName());

    private final int port;
    private final ExecutorService executorService;
    private final Dispatcher dispatcher;

    private final String baseStaticPath;

    public HttpServer(int port) throws IOException {
        this.port = port;
        this.executorService = Executors.newCachedThreadPool(); // сервис-исполнитель обработчиков запросов
        dispatcher = new Dispatcher();

        var file = new File(System.getProperty("user.dir") + "/static");
        if (file.exists() && file.isDirectory()) {
            baseStaticPath = file.getCanonicalPath();
        } else {
            String msg = String.format("Путь %s не существует или это не директория", file.getCanonicalPath());
            throw new RuntimeException(msg);
        }
    }

    public void registerRoute(HttpMethod method, String routePathRegex, RequestProcessor processor) {
        dispatcher.registerRoute(method, routePathRegex, processor);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            String msg = "Сервер запущен на порту: " + port;
            logger.info(msg);
            System.out.println(msg);

            while (true) {
                Socket socket = serverSocket.accept();
                enqueueTaskForRequest(socket);
            }
        } catch (IOException e) {
            logger.fatal("Исключение", e);
        }
    }

    private void enqueueTaskForRequest(Socket socket) {
        executorService.execute(() -> {
            try (Socket ignored = socket;
                 OutputStream out = socket.getOutputStream();
                 InputStream in = socket.getInputStream()) {
                HttpRequest request = getRequest(in);
                if (request != null) {
                    HttpResponse response = new HttpResponse();
                    processRequest(request, out, response);
                }
            } catch (IOException e) {
                logger.error("enqueueTaskForRequest: ", e);
            }
        });
    }

    private void processRequest(HttpRequest request, OutputStream out, HttpResponse response) {
        logger.info("Start request ({}) processing", request.getUri());
        try {
            if (!tryGetFile(request, out)) {
                dispatchNonFileRequest(request, out, response);
            }
        } catch (Exception e) {
            logger.error("End on request ({}) processing. Exception: {}", request.getUri(), e);
        } finally {
            logger.info("End request ({}) processing. status: {} ({}; {})", request.getUri(), response.getStatus(), response.getStatusText(), response.getErrorMessage());
        }
    }

    private HttpRequest getRequest(InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        int n = in.read(buffer);
        if (n > 0) {
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            String msg = request.info(true);
            logger.debug(msg);
            return request;
        }
        return null;
    }

    private boolean tryGetFile(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        if (httpRequest.getMethod() != HttpMethod.GET) {
            return false;
        }

        String filePath = baseStaticPath + httpRequest.getUri();

        var file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        filePath = file.getCanonicalPath();
        if (!filePath.startsWith(baseStaticPath + File.separator)) {
            return false;
        }

        logger.debug("File `{}` exists\n", filePath);

        String mimeType = Files.probeContentType(file.toPath());

        String response = String.format("HTTP/1.1 200 OK\r\nContent-Type: %s; charset=utf-8\r\n\r\n", mimeType);
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));

        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[4096]; // 4KB
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, lengthRead);
                outputStream.flush();
            }
        }
        return true;
    }

    private void dispatchNonFileRequest(HttpRequest request, OutputStream out, HttpResponse response) throws IOException {
        try {
            dispatcher.execute(request, response);
        } catch (NotFoundException e) {
            response.setErrorStatus(404, "Not found", e.getErrorMessage());
        } catch (BadRequestException e) {
            response.setErrorStatus(400, "Bad Request", e.getErrorMessage());
        } catch (Exception e) {
            response.setErrorStatus(500, "Internal Server Error", e.getMessage());
        }
        response.writeTo(out);
    }
}