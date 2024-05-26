package ru.victoriaknyazeva.otus.webserver;

public class NotFoundException extends Exception {

    public NotFoundException() {
    }

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public String getErrorMessage() {
        return getMessage();
    }
}