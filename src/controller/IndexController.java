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

    @Route(name = "index.home")
    public Response indexAction(Request request) {
        return this.render("@view/home");
    }

    @Route(name = "index.error", path = "/error/{code}")
    public Response errorAction(Request request, @Argument(mask = "\\d{3}", name = "code") String error) {
        this.context.put("code", error);
        return this.render("@error/404");
    }

}
