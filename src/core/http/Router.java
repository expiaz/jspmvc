package core.http;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private List<Route> routes;

    private Route defaultRoute;

    public Router() {

        this.defaultRoute = new Route(
                "default",
                Pattern.compile(".*"),
                controller.IndexController.class,
                "error",
                HttpMethod.GET
        );
        this.routes = new ArrayList<>();
    }

    public void add(String pattern, Class controller, String action, HttpMethod method, String name) {
        routes.add(new Route(name, Pattern.compile(pattern), controller, action, method));
    }

    public void get(String pattern, Class controller, String action, String name) {
        this.add(pattern, controller, action, HttpMethod.GET, name);
    }

    public void post(String pattern, Class controller, String action, String name) {
        this.add(pattern, controller, action, HttpMethod.POST, name);
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

        return new Match(this.defaultRoute, parameters);
    }

}
