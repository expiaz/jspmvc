package core.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private List<Route> routes;

    public Router() {
        this.routes = new ArrayList<>();
    }

    public void add(String pattern, Class controller, Method action, HttpMethod method, String name) {
        routes.add(new Route(name, pattern, controller, action, method));
    }

    public void get(String pattern, Class controller, String action, String name) throws NoSuchMethodException {
        String actionName = action.endsWith("Action") ? action : action + "Action";
        this.add(pattern, controller, controller.getMethod(actionName), HttpMethod.GET, name);
    }

    public void post(String pattern, Class controller, String action, String name) throws NoSuchMethodException {
        String actionName = action.endsWith("Action") ? action : action + "Action";
        this.add(pattern, controller, controller.getMethod(actionName), HttpMethod.POST, name);
    }

    public void setup() {
        this.routes.sort((a, b) -> a.getPattern().toString().length() >= b.getPattern().toString().length() ? 1 : -1);
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

        for(Route r : this.routes) {
            //TODO add check parameters AND map route hash name
            if(r.getName().equals(route)) {
                Map<String, String> realMap = new HashMap<>();
                for(String[] entry : arguments){
                    realMap.put(entry[0], entry[1]);
                }
                return r.build(realMap);
            }
        }

        throw new NoSuchElementException(route + "isn't a known route name");
    }

}
