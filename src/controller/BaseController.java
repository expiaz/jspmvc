package controller;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.annotations.Inject;
import core.utils.ParameterBag;
import core.utils.Renderer;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    private Response response;
    private Request request;

    Renderer renderer;
    Router router;

    ParameterBag context;

    public BaseController(@Inject Renderer renderer, @Inject Router router,
                             @Inject Request request, @Inject Response response) {
        this.renderer = renderer;
        this.router = router;
        this.request = request;
        this.response = response;
        this.context = new ParameterBag();
    }

    Response render(String viewPath, ParameterBag arguments) {
        for(Map.Entry<String, Object> entry : arguments.entrySet()) {
            this.context.put(entry.getKey(), entry.getValue());
        }
        return this.render(viewPath);
    }

    Response render(String viewPath) {
        for (Map.Entry<String, Object> entry : this.context.entrySet()) {
            this.request.getRequest().setAttribute(entry.getKey(), entry.getValue());
        }
        return this.response.render(this.renderer.render(viewPath));
    }

    Response redirect(String url) {
        return this.response.redirect(url);
    }

    Response notFound() {
        return this.response.redirect(this.router.build("@index/error", new ParameterBag().add("code", 404)));
    }

    public Response getResponse(){
        return this.response;
    }

}
