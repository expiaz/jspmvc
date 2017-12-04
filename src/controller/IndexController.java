package controller;

import core.utils.Argument;
import core.utils.Renderer;
import core.utils.Route;
import core.utils.Viewspace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Viewspace(namespace = "hello", path = "@view/hello/")
public class IndexController extends BaseController {

    public IndexController(Renderer renderer, HttpServletRequest request) {
        super(renderer, request);
    }

    @Route(name = "error", path = "/error")
    public String errorAction(HttpServletRequest request, HttpServletResponse response){
        return this.render("@shared/error404");
    }

    @Route(name = "index")
    public String indexAction(HttpServletRequest request, HttpServletResponse response){
        return this.render("@hello/i");
    }

    @Route(name = "index.hello", path = "/hello/{name}/")
    public String helloAction(HttpServletRequest request, HttpServletResponse response, @Argument(mask = "/") String name){
        this.context.put("name", name);
        return this.render("@hello/hello");
    }

}
