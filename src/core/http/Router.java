package core.http;

import core.utils.Container;
import core.utils.ParameterBag;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

public class Router {

    private Container container;

    private Map<String, Route> routesByName;
    private List<Route> routes;

    public Router(Container container) {
        this.container = container;
        this.routes = new ArrayList<>();
        this.routesByName = new HashMap<>();
    }

    public void add(String pattern, Class controller, Method action, HttpMethod method, String name) {
        Route route = new Route(name, pattern, controller, action, method);
        routesByName.put(name, route);
        routes.add(route);
    }

    public void setup() {
        routes.sort((a, b) -> a.getPattern().toString().length() >= b.getPattern().toString().length() ? 1 : -1);
    }

    public Match match(Request request) {

        Matcher m;
        List<RouteArgument> routeParameters;

        for(Route route : this.routes) {
            if(route.getMethod() == request.getMethod()) {
                // match the url to the pattern, collect the groups and call method with requ resp and params
                m = route.getPattern().matcher(request.getPath());
                if(m.matches()) {
                    ParameterBag parameters = new ParameterBag();
                    routeParameters = route.getParameters();
                    for(int i = 0; i < m.groupCount(); i++) {
                        // i + 1 to pass over the base group
                        parameters.add(routeParameters.get(i).getName(), m.group(i + 1));
                    }
                    return new Match(route, parameters);
                }
            }
        }

        return null;
    }

    public String build(String route) {
        return this.build(route, new ParameterBag());
    }

    public String build(String route, ParameterBag arguments) {
        if(! this.routesByName.containsKey(route)) {
            throw new NoSuchElementException(route + " isn't a known route name");
        }

        Route r = this.routesByName.get(route);

        Request request = (Request) this.container.get(Request.class);

        return request.getContextPath() + r.build(arguments);
    }

    public String build(String route, ParameterBag arguments, ParameterBag query) {
        String queryString = "";

        for(Map.Entry<String, Object> entry : query.entrySet()) {
            if(queryString.length() == 0) {
                queryString += "?" + entry.getKey() + "=" + entry.getValue().toString();
            } else {
                queryString += "&" + entry.getKey() + "=" + entry.getValue().toString();
            }
        }

        return this.build(route, arguments) + queryString;
    }

}
