package ru.victoriaknyazeva.otus.webserver.routing;

import ru.victoriaknyazeva.otus.webserver.HttpMethod;
import ru.victoriaknyazeva.otus.webserver.HttpRequest;
import ru.victoriaknyazeva.otus.webserver.application.processors.RequestProcessor;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сопоставляет метод и шаблон пути с обработчиком запроса.
 * Проверяет соответствие URI запроса шаблону.
 */
public class Route {
    private final String routeTemplate;
    private final Pattern pattern;
    public final HttpMethod method;
    public final RequestProcessor processor;

    /**
     * Набор имен из шаблона запроса
     */
    private final HashSet<String> routeParameterNames;

    public Route(HttpMethod method, String routeTemplate, RequestProcessor processor) throws RuntimeException {
        this.method = method;
        this.processor = processor;
        this.routeTemplate = routeTemplate;
        this.routeParameterNames = new HashSet<>();

        // регулярка для извлечение имен {name} из шаблона запроса вида ../{name}
        final String regex = "(\\{\\w+\\})";

        final Pattern templatePattern = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher matcher = templatePattern.matcher(routeTemplate);

        // исходно совпадает с шаблоном роута
        String routeRegex = this.routeTemplate;
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                var bracedName = matcher.group(i); // получили {name}

                var name = bracedName.substring(1, bracedName.length() - 1); // отсекаем фигурные скобки
                if (routeParameterNames.contains(name)) {
                    String msg = String.format("шаблон %s содержит подстановку %s более одного раза", routeTemplate, bracedName);
                    throw new RuntimeException(msg);
                }
                routeParameterNames.add(name);

                // часть регулярки для извлечения значения параметра с именем name
                var replacement = String.format("(?<%s>[^/?]+)", name);

                // заменяет вхождение {name} на регулярку выше
                routeRegex = routeRegex.replace(bracedName, replacement);
            }
        }
        routeRegex = routeRegex + "/*(\\?.*)*$";
        /* дополнение регулярки для проверки хвоста URI, такое что для шаблона "/items/{id}":
            /items/6 - OK
            /items/6? - OK
            /items/6?qwe - OK
            /items/6/? - OK
            /items/6/?sdf=234 - OK
            /items/6/yqwtre - BAD!
            /items/6yqwtre - OK
         */
        pattern = Pattern.compile(routeRegex, Pattern.DOTALL); //полная регулярка для извлечения значений параметров роутов
    }

    public String getRouteTemplate() {
        return routeTemplate;
    }

    public boolean match(HttpRequest request) {
        if (method != request.getMethod()) {
            return false;
        }
        Matcher matcher = pattern.matcher(request.getUri());
        boolean result = false;
        while (matcher.find()) {
            result = true;
            for (String name : routeParameterNames) {
                String value = matcher.group(name);
                if (value != null) {
                    request.addRouteParameter(name, value);
                }
            }
        }
        return result;
    }
}