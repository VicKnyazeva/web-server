package ru.victoriaknyazeva.otus.webserver;

public class Main {
    // Домашнее задание:
    // - Основное (с сайта или презентации)
    // - (*) В случае UnknownOperationRequestProcessor верните статус 404 со страницей NOT FOUND

    public static void main(String[] args) {
        new HttpServer(8189).start();
    }
}