package core.http;

import core.annotations.Parameter;

public class RouteArgument {

    private Parameter annotation;
    private int start;
    private int end;

    RouteArgument(Parameter annotation, int start, int end){
        this.annotation = annotation;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return annotation.name();
    }

    public String getMask() {
        return annotation.mask();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
