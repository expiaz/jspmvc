package controller;

import core.annotations.Parameter;
import core.annotations.Inject;
import core.annotations.Route;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.ParameterBag;
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
    public Response errorAction(Request request, @Parameter(mask = "\\d{3}", name = "code") String error) {
        return this.render("@error/404",
                new ParameterBag()
                    .add("code", error)
        );
    }

}
