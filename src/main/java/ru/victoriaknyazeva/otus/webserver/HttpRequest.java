package ru.victoriaknyazeva.otus.webserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private String rawRequest;
    private String uri;
    private HttpMethod method;

    /**
     * Именнованные параметры, извлеченные из URI запроса в случае, если было совпадение с шаблоном
     */
    private Map<String, String> routeParameters;

    /**
     * Именнованные параметры, извлеченные из query-части URI
     */
    private Map<String, String> queryParameters;
    private String body;

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String addRouteParameter(String key, String value) {
        return routeParameters.put(key, value);
    }

    public String getRouteParameter(String key) {
        return routeParameters.get(key);
    }
    public String getQueryParameter(String key) {
        return queryParameters.get(key);
    }

    public String getBody() {
        return body;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.routeParameters = new HashMap<>();
        this.parseRequestLine();
        this.tryToParseBody();
    }

    public void tryToParseBody() {
        if (method == HttpMethod.GET) {
            return;
        }
        List<String> lines = rawRequest.lines().collect(Collectors.toList());
        int splitLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isEmpty()) {
                splitLine = i;
                break;
            }
        }
        if (splitLine > -1) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = splitLine + 1; i < lines.size(); i++) {
                stringBuilder.append(lines.get(i));
            }
            this.body = stringBuilder.toString();
        }
    }

    public void parseRequestLine() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        this.uri = rawRequest.substring(startIndex + 1, endIndex);
        this.method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));
        this.queryParameters = new HashMap<>();
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            this.uri = elements[0];
            if (elements.length > 1) {
                String[] keysValues = elements[1].split("&");
                for (String o : keysValues) {
                    String[] keyValue = o.split("=");
                    if (keyValue.length == 2)
                        this.queryParameters.put(keyValue[0], keyValue[1]);
                    else
                        this.queryParameters.put(keyValue[0], "");
                }
            }
        }
    }

    public String info(boolean showRawRequest) {
        StringBuilder sb = new StringBuilder();
        if (showRawRequest) {
            sb.append(rawRequest + "\n");
        }
        sb.append("URI: " + uri + "\n");
        sb.append("HTTP-method: " + method + "\n");
        sb.append("Parameters: " + queryParameters + "\n");
        sb.append("Body: " + body + "\n");
        return sb.toString();
    }
}