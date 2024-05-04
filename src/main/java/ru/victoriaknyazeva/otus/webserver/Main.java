package ru.victoriaknyazeva.otus.webserver;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("This process id: " + ProcessHandle.current().pid());

        ExecutorService executorService = Executors.newCachedThreadPool(); // сервис-исполнитель обработчиков запросов
        var server = new HttpServer(8189, executorService);

        registerShutdownHooks(server);

        server.start();

        System.out.println("Нажмите CTRL+C чтобы остановить сервер");

        server.waitForStop();

        executorService.shutdownNow(); // "грубо" завершаем все запущенные обработчики запросов

        System.out.println("Сервер остановлен");
    }

    private static void registerShutdownHooks(HttpServer server) {
        SignalHandler handler = new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Сервер остановливается по сигналу системы: " + sig);
                server.stop();
            }
        };
        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("TERM"), handler);
    }
}