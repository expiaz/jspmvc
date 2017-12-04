package core.http;

import core.utils.Context;
import core.utils.Contextual;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

public class Router implements Contextual {

    private Context currentContext;

    private Map<String, Route> routesByName;
    private List<Route> routes;

    public Router() {
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

    public Match match(HttpServletRequest request) {

        Matcher m;
        List<String> parameters = new ArrayList<>();
        String path = request.getPathInfo();

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        for(Route route : this.routes) {
            if(route.getMethod().toString().equals(request.getMethod())) {
                // match the url to the pattern, collect the groups and call method with requ resp and params
                m = route.getPattern().matcher(path);
                if(m.matches()) {
                    for(int i = 0; i < m.groupCount(); i++) {
                        // i + 1 to pass over the base group
                        parameters.add(m.group(i + 1));
                    }
                    return new Match(route, parameters);
                }
            }
        }

        return null;
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

        String prefix = this.currentContext.getRequestUri();
        String needle = this.currentContext.getPathInfo();
        int index = prefix.indexOf(needle);
        prefix = prefix.substring(0, index);

        return prefix + r.build(realMap);
    }

    public String redirect(String route, String[][] arguments) {
        try {
            this.currentContext.getRequest().getRequestDispatcher(this.build(route, arguments)).forward(this.currentContext.getRequest(), this.currentContext.getResponse());
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "__REDIRECT__";
    }

    public String redirect(String route) {
        return this.redirect(route, new String[][]{});
    }

    @Override
    public void setContext(Context context) {
        this.currentContext = context;
    }
}
