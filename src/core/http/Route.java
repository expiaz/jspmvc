package core.http;

import controller.BaseController;
import core.FrontController;
import core.annotations.Parameter;
import core.utils.Container;
import core.utils.ParameterBag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private List<Class<? extends Middleware>> before;
    private List<Class<? extends Middleware>> after;

    private String path;
    private String name;
    private Pattern pattern;
    private Class<? extends BaseController> controller;
    private Method action;
    private HttpMethod method;

    public Route(String name, String path, Class<? extends BaseController> controller, Method action, HttpMethod method, List<Class<? extends Middleware>> before, List<Class<? extends Middleware>> after) {
        this.name = name;
        this.controller = controller;
        this.action = action;
        this.method = method;
        this.arguments = new ArrayList<>();

        this.before = before;
        this.after = after;

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
            pattern = pattern.substring(0, m.start() - offset) + "(" + pArg.mask() + ")" + pattern.substring(m.end() - offset);
            // increment offset by the size of the missing characters (fullgroup - replaced group)
            offset += m.group(0).length() - (pArg.mask().length() + 2) /* for '(' and ')' */;
        }

        if(methodArguments.size() != this.arguments.size()) {
            throw new InvalidParameterException(controller.getName() + "::" + action.getName() + " expected " + methodArguments.size() + " parameters but route got " + this.arguments.size());
        }

        this.pattern = Pattern.compile(pattern);
    }

    private Middleware instanciateMiddleware(Container container, Class<? extends Middleware> middlewareClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return middlewareClass.getConstructor(Container.class).newInstance(container);
    }

    public Middleware getMiddlewareStack(Container container, Middleware controller)
    {
        try {

            Middleware first, last, current;
            if (this.before.size() > 0) {
                first = last = this.instanciateMiddleware(container, this.before.get(0));
                for(int i = 1; i < this.before.size(); ++i) {
                    current = this.instanciateMiddleware(container, this.before.get(i));
                    last.setNext(current);
                    last = current;
                }
                last.setNext(controller);
                last = controller;
            } else {
                first = last = controller;
            }

            for (Class<? extends Middleware> middleware : this.after) {
                current = this.instanciateMiddleware(container, middleware);
                last.setNext(current);
                last = current;
            }

            return first;

        } catch (Exception e) {
            FrontController.die(Route.class, e);
            return null;
        }
    }

    public String build(ParameterBag arguments) {
        String path = this.path;
        int offset = 0;

        for(RouteArgument arg : this.arguments) {
            // replace the {name} by arguments.get(name)
            path = path.substring(0, arg.getStart() - offset) + arguments.get(arg.getName()).toString() + path.substring(arg.getEnd() - offset);
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

    public Class<? extends BaseController> getController() {
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

