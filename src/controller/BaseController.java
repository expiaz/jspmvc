package controller;

import core.utils.Renderer;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    protected HttpServletRequest request;
    protected Renderer renderer;

    protected Map<String, Object> context;

    protected BaseController(core.utils.Renderer renderer, HttpServletRequest request) {
        this.renderer = renderer;
        this.request = request;
        this.context = new HashMap<>();
    }

    protected String render(String viewPath) {
        for (Map.Entry<String, Object> entry : this.context.entrySet()) {
            this.request.setAttribute(entry.getKey(), entry.getValue());
        }
        return this.renderer.render(viewPath);
    }

}
