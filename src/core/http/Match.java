package core.http;

import core.utils.ParameterBag;

public class Match {

    private Route route;
    private ParameterBag parameters;

    public Match(Route route, ParameterBag parameters) {
        this.route = route;
        this.parameters = parameters;
    }

    public Route getRoute() {
        return route;
    }

    public ParameterBag getParameters() {
        return parameters;
    }
}
