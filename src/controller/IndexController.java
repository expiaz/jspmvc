package controller;

import core.utils.Renderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexController extends BaseController {

    public IndexController(Renderer renderer, HttpServletRequest request) {
        super(renderer, request);
    }

    public String errorAction(HttpServletRequest request, HttpServletResponse response, List<String> parameters){
        return this.render("@shared/error404");
    }

    public String helloAction(HttpServletRequest request, HttpServletResponse response, List<String> parameters){
        this.context.put("name", parameters.get(0));
        return this.render("@hello/hello");
    }

}
