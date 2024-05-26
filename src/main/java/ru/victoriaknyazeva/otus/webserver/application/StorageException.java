package ru.victoriaknyazeva.otus.webserver.application;

public class StorageException extends RuntimeException {
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
