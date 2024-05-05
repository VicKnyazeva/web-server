package ru.victoriaknyazeva.otus.webserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        int port = Integer.parseInt((String)System.getProperties().getOrDefault("port", "8189"));

        logger.debug("This process id: {}", ProcessHandle.current().pid());

        ExecutorService executorService = Executors.newCachedThreadPool(); // сервис-исполнитель обработчиков запросов
        var server = new HttpServer(port, executorService);

        registerShutdownHooks(server);

        server.start();

        System.out.println("Нажмите CTRL+C чтобы остановить сервер");

        server.waitForStop();

        executorService.shutdownNow(); // "грубо" завершаем все запущенные обработчики запросов

        logger.info("Сервер остановлен");
    }

    private static void registerShutdownHooks(HttpServer server) {
        SignalHandler handler = new SignalHandler() {
            public void handle(Signal sig) {
                logger.info("Сервер остановливается по сигналу системы: {}", sig);
                server.stop();
            }
        };
        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("TERM"), handler);
    }
}