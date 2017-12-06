package core.http;

import core.utils.Argument;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {

    private static Pattern argumentsRegExp;
    static {
        argumentsRegExp = Pattern.compile("\\{(\\w+)\\}");
    }

    private List<RouteArgument> arguments;

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

        // extract arguments of the route method action
        Map<String, String> methodArguments = new HashMap<>();
        for(Parameter p : action.getParameters()) {
            if(p.isAnnotationPresent(Argument.class)) {
                Argument a = p.getAnnotation(Argument.class);
                String aName = a.name();
                /*if(aName.equals("__DEFAULT__")) {
                    aName = p.getName();
                }*/
                methodArguments.put(aName, a.mask());
            }
        }

        String pattern = path;
        RouteArgument rarg;
        int offset = 0;
        String argRegexp;
        String argName;

        Matcher m = argumentsRegExp.matcher(path);
        while(m.find()) {
            argName = m.group(1);
            if(! methodArguments.containsKey(argName)) {
                throw new InvalidParameterException(controller.getName() + "::" + action.getName() + " parameter " + argName + " not found");
            }
            argRegexp = methodArguments.get(argName);
            rarg = new RouteArgument(argName, argRegexp, m.start(), m.end());
            this.arguments.add(rarg);
            // replace the {name} by (mask)
            pattern = pattern.substring(offset, m.start()) + "(" + argRegexp + ")" + pattern.substring(m.end() - offset);
            // increment offset by the size of the missing characters (fullgroup - replaced group)
            offset += m.end() - m.start() - argRegexp.length();
        }

        if(methodArguments.size() != this.arguments.size()) {
            throw new InvalidParameterException(controller.getName() + "::" + action.getName() + " expected " + methodArguments.size() + " but route got " + this.arguments.size());
        }

        this.pattern = Pattern.compile(pattern);
    }

    String build(Map<String, String> arguments) {
        String path = this.path;
        int offset = 0;

        for(RouteArgument arg : this.arguments) {
            // replace the {name} by (mask)
            path = path.substring(offset, arg.start) + arguments.get(arg.name) + path.substring(arg.end - offset);
            // increment offset by the size of the missing characters (fullgroup - replaced group)
            offset += arg.end - arg.start - arg.name.length();
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

    public HttpMethod getMethod() {
        return method;
    }

    private class RouteArgument {

        String name;
        String mask;
        int start;
        int end;

        RouteArgument(String name, String mask, int start, int end){
            this.name = name;
            this.mask = mask;
            this.start = start;
            this.end = end;
        }
    }
}

