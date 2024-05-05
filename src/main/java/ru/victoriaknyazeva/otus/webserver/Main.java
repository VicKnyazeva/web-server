package ru.victoriaknyazeva.otus.webserver;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    // Домашнее задание:
    // - Добавить логирование (с правильным выбором уровня логирования для сообщений)
    // - Сделайте так, чтобы Request по методу понимал имеет ли смысл вообще искать body в запросе (в GET запросе body не должно быть)
    // - * При получении PUT /products обновите данные продукта
    // PUT:
    // {
    //   "id": "4b798830-d2ad-4ee1-b4b9-03866cb75596",
    //   "title": "new-name",
    //   "price": 1
    // }
    // У продукта с id = 4b798830-d2ad-4ee1-b4b9-03866cb75596 поля должны быть изменены на те значения, что пришли в теле PUT запроса

    // фронт, джар, логирование и параметризированный запуск сервера через консоль

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