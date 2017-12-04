package controller;

import core.http.Router;
import core.utils.Argument;
import core.utils.Renderer;
import core.utils.Route;
import core.utils.Viewspace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Viewspace(namespace = "hello", path = "@view/hello/")
public class IndexController extends BaseController {

    public IndexController(Renderer renderer, Router router, HttpServletRequest request) {
        super(renderer, router, request);
    }

    @Route(name = "error", path = "/error")
    public String errorAction(HttpServletRequest request, HttpServletResponse response){
        return this.render("@shared/error404");
    }

    @Route(name = "index")
    public String indexAction(HttpServletRequest request, HttpServletResponse response){
        return this.render("@view/index");
    }

    @Route(name = "index.hello", path = "/hello/{name}/")
    public String helloAction(HttpServletRequest request, HttpServletResponse response,
                              @Argument(mask = "\\w{3}", name = "name") String name
    ){
        this.context.put("name", name);
        return this.render("@hello/hello");
    }

    @Route(name = "redirect", path = "/redirect")
    public String redirectAction(HttpServletRequest request, HttpServletResponse response){
        return this.router.redirect("@hello/hello", new String[][]{
            new String[]{"name", "jea"}
        });
    }

}
