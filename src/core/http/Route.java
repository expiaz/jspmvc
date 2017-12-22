package core.http;

import core.annotations.Parameter;
import core.utils.Filter;
import core.utils.ParameterBag;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static core.utils.Filter.*;

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
        Map<String, Parameter> methodArguments = new HashMap<>();
        for(java.lang.reflect.Parameter p : action.getParameters()) {
            if(p.isAnnotationPresent(Parameter.class)) {
                Parameter a = p.getAnnotation(Parameter.class);
                String aName = a.name();
                /*if(aName.equals("__DEFAULT__")) {
                    aName = p.getName();
                }*/
                methodArguments.put(aName, a);
            }
        }

        String pattern = path;
        RouteArgument rarg;
        int offset = 0;
        String argName;

        Matcher m = argumentsRegExp.matcher(path);
        while(m.find()) {
            argName = m.group(1);
            if(! methodArguments.containsKey(argName)) {
                throw new InvalidParameterException(controller.getName() + "::" + action.getName() + " parameter " + argName + " not found");
            }
            Parameter pArg = methodArguments.get(argName);
            rarg = new RouteArgument(pArg, m.start(), m.end());
            this.arguments.add(rarg);
            // replace the {name} by (mask)
            pattern = pattern.substring(offset, m.start()) + "(" + pArg.mask() + ")" + pattern.substring(m.end() - offset);
            // increment offset by the size of the missing characters (fullgroup - replaced group)
            offset += m.end() - m.start() - pArg.mask().length();
        }

        if(methodArguments.size() != this.arguments.size()) {
            throw new InvalidParameterException(controller.getName() + "::" + action.getName() + " expected " + methodArguments.size() + " parameters but route got " + this.arguments.size());
        }

        this.pattern = Pattern.compile(pattern);
    }

    public String build(ParameterBag arguments) {
        String path = this.path;
        int offset = 0;

        for(RouteArgument arg : this.arguments) {
            // replace the {name} by (mask)
            path = path.substring(offset, arg.getStart()) + arguments.get(arg.getName()).toString() + path.substring(arg.getEnd() - offset);
            // increment offset by the size of the missing characters (fullgroup - replaced group)
            offset += arg.getEnd() - arg.getStart() - arg.getName().length();
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

    public List<RouteArgument> getParameters() {
        return this.arguments;
    }

}

