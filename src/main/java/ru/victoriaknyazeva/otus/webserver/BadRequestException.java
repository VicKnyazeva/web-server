package ru.victoriaknyazeva.otus.webserver;

public class BadRequestException extends Exception {

    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorMessage() {
        if (getCause() != null) {
            return getCause().getMessage();
        }
        return getMessage();
    }
}