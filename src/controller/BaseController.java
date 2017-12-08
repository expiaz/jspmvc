package controller;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.annotations.Inject;
import core.utils.Renderer;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    private Response response;
    private Request request;

    Renderer renderer;
    Router router;

    Map<String, Object> context;

    public BaseController(@Inject Renderer renderer, @Inject Router router,
                             @Inject Request request, @Inject Response response) {
        this.renderer = renderer;
        this.router = router;
        this.request = request;
        this.response = response;
        this.context = new HashMap<>();
    }

    Response render(String viewPath, String[][] arguments) {
        for(String[] arg : arguments) {
            this.context.put(arg[0], arg[1]);
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
        return this.response.redirect(this.router.build("@index/error", new String[][] {
            new String[] {"code", "404"}
        }));
    }

    public Response getResponse(){
        return this.response;
    }

}
