package core.http;

import java.util.List;

public class Match {

    private Route route;
    private List<String> parameters;

    public Match(Route route, List<String> parameters) {
        this.route = route;
        this.parameters = parameters;
    }

    public Route getRoute() {
        return route;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
