package core.http;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {

    private static Pattern argumentsRegExp;
    static {
        argumentsRegExp = Pattern.compile("\\{(\\w+): ([^}]+)\\}");
    }

    private List<List<Object>> arguments;

    private String path;
    private String name;
    private Pattern pattern;
    private Class controller;
    private Method action;
    private HttpMethod method;

    public Route(String name, String path, Class controller, Method action, HttpMethod method) {
        this.name = name;
        this.controller = controller;
        this.action = action;
        this.method = method;
        this.arguments = new ArrayList<>();

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        this.path = path;

        String pattern = path;
        List<Object> entry;
        List<List<Object>> patterns = new ArrayList<>();

        Matcher m = argumentsRegExp.matcher(path);
        while(m.find()) {
            entry = new ArrayList<>();
            entry.add(m.group(1));
            entry.add(m.start());
            entry.add(m.end());
            arguments.add(entry);

            entry = new ArrayList<>();
            entry.add(m.group(2));
            entry.add(m.start());
            entry.add(m.end());
            patterns.add(entry);
        }

        if(patterns.size() > 0) {
            for(int i = patterns.size() - 1; i >= 0; i--) {
                entry = patterns.get(i);
                pattern = pattern.substring(0, (Integer) entry.get(1)) + "(" + entry.get(0).toString() + ")" + pattern.substring((Integer) entry.get(2));
            }
        }

        this.pattern = Pattern.compile(pattern);
    }

    public String build(Map<String, String> arguments) {

        String path = this.path;

        if(this.arguments.size() > 0) {
            List<Object> entry;
            for(int i = this.arguments.size() - 1; i >= 0; i--) {
                entry = this.arguments.get(i);
                path = path.substring(0, (Integer) entry.get(1)) + arguments.get(entry.get(0).toString()) + path.substring((Integer) entry.get(2));
            }
        }

        return path;
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

    public Method getAction() {
        return action;
    }

    public core.http.HttpMethod getMethod() {
        return method;
    }
}
