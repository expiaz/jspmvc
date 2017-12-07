package core.http;

import core.utils.Container;

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

        for(Route route : this.routes) {
            if(route.getMethod() == request.getMethod()) {
                // match the url to the pattern, collect the groups and call method with requ resp and params
                m = route.getPattern().matcher(request.getPath());
                if(m.matches()) {
                    String[] parameters = new String[route.getNumberOfExpectedParameters()];
                    for(int i = 0; i < m.groupCount(); i++) {
                        // i + 1 to pass over the base group
                        parameters[i] = m.group(i + 1);
                    }
                    return new Match(route, parameters);
                }
            }
        }

        return null;
    }

    public String build(String route) {
        return this.build(route, new String[][] {});
    }

    public String build(String route, String[][] arguments) {
        if(! this.routesByName.containsKey(route)) {
            throw new NoSuchElementException(route + "isn't a known route name");
        }

        Route r = this.routesByName.get(route);
        Map<String, String> realMap = new HashMap<>();
        for(String[] entry : arguments){
            realMap.put(entry[0], entry[1]);
        }

        Request request = (Request) this.container.get(Request.class);

        return request.getContextPath() + r.build(realMap);
    }

    public String build(String route, String[][] arguments, String[][] query) {
        String queryString = "";
        for(String[] q : query) {
            if(queryString.length() == 0) {
                queryString += "?" + q[0] + "=" + q[1];
            } else {
                queryString += "&" + q[0] + "=" + q[1];
            }
        }

        return this.build(route, arguments) + queryString;
    }

    public String build(String route, String[][] arguments, String[][] query, String hash) {
        return this.build(route, arguments, query) + "#" + hash;
    }
}
