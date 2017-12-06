package controller;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.*;

@Viewspace(namespace = "hello", path = "@view/hello/")
public class IndexController extends BaseController {

    public IndexController(@Inject Renderer renderer, @Inject Router router,
                           @Inject Request request, @Inject Response response) {
        super(renderer, router, request, response);
    }

    @Route(name = "error", path = "/error")
    public Response errorAction(Request request, Response response){
        return this.render("@shared/error404");
    }

    @Route(name = "index")
    public Response indexAction(Request request, Response response){
        return this.render("@view/index");
    }

    @Route(name = "index.hello", path = "/hello/{name}/")
    public Response helloAction(Request request, Response response,
                              @Argument(mask = "\\w+", name = "name") String name
    ){
        this.context.put("name", name);
        return this.render("@hello/hello");
    }

    @Route(name = "redirect", path = "/redirect")
    public Response redirectAction(Request request, Response response){
        return this.redirect(this.router.build("index.hello", new String[][]{
            new String[]{"name", "jean"}
        }));
    }

}
