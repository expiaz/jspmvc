package core.http;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class Route {

    private String name;
    private Pattern pattern;
    private Class controller;
    private String action;
    private HttpMethod method;

    public Route(String name, Pattern pattern, Class controller, String action, HttpMethod method) {
        this.name = name;
        this.pattern = pattern;
        this.controller = controller;
        this.action = action;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Class getController() {
        return controller;
    }

    public String getAction() {
        return action;
    }

    public core.http.HttpMethod getMethod() {
        return method;
    }
}
