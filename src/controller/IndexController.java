package controller;

import core.annotations.Argument;
import core.annotations.Inject;
import core.annotations.Route;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.Renderer;

public class IndexController extends BaseController {

    public IndexController(@Inject Renderer renderer, @Inject Router router,
                           @Inject Request request, @Inject Response response) {
        super(renderer, router, request, response);
    }

    @Route(name = "index")
    public Response indexAction(Request request, Response response) {
        return this.render("@view/index");
    }

    @Route(name = "error", path = "/error/{code}")
    public Response errorAction(Request request, Response response, @Argument(mask = "\\d{3}", name = "code") String error) {
        this.context.put("code", error);
        return this.render("@error/404");
    }

}
