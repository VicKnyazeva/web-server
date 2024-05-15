package ru.victoriaknyazeva.otus.webserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private int status;
    private String statusText;
    private String contentType;
    private String errorMessage;
    public final ByteArrayOutputStream body;

    public HttpResponse() {
        status = 200;
        statusText = "OK";
        contentType = "text/html; charset=utf-8";
        body = new ByteArrayOutputStream();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }

    public void setErrorStatus(int status, String statusText, String errorMessage) {
        this.status = status;
        this.statusText = statusText;
        this.errorMessage = errorMessage;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void writeTo(OutputStream output) throws IOException {
        if (status == 200) {
            String header = String.format("HTTP/1.1 %d %s\r\nContent-Type: %s\r\n\r\n",
                    status, statusText, contentType);
            output.write(header.getBytes(StandardCharsets.UTF_8));
            output.write(body.toByteArray());
        } else {
            String message = errorMessage;
            if (message == null) {
                message = "";
            }
            String content = String.format("HTTP/1.1 %d %s\r\nContent-Type: text/plain; charset=utf-8\r\n\r\n%d. %s\n\n%s",
                    status, statusText, status, statusText, message);
            output.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }
}