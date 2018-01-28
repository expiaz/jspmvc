package core.http;

import core.annotations.Inject;
import core.utils.Container;

public abstract class Middleware {

    protected Container container;
    private Middleware next = null;

    public Middleware(@Inject Container container) {
        this.container = container;
    }

    public abstract Response apply(Request request, Response response);

    public Middleware getNext() {
        if(this.next == null) {
            // tail of the queue
            this.next = new Middleware(this.container) {
                @Override
                public Response apply(Request request, Response response) {
                    return response;
                }
            };
        }

        return next;
    }

    public Middleware setNext(Middleware next) {
        this.next = next;
        return this;
    }
}
