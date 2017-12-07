package core.http;

import java.util.List;

public class Match {

    private Route route;
    private String[] parameters;

    public Match(Route route, String[] parameters) {
        this.route = route;
        this.parameters = parameters;
    }

    public Route getRoute() {
        return route;
    }

    public String[] getParameters() {
        return parameters;
    }
}
