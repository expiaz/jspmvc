package controller;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.annotations.Inject;
import core.utils.NotificationType;
import core.utils.ParameterBag;
import core.utils.Renderer;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    public static final String FLASH_BAG = "__FLASH__";

    private Response response;
    private Request request;

    Renderer renderer;
    Router router;

    ParameterBag context;
    ParameterBag flashBag;

    public BaseController(@Inject Renderer renderer, @Inject Router router,
                             @Inject Request request, @Inject Response response) {
        this.renderer = renderer;
        this.router = router;
        this.request = request;
        this.response = response;
        this.flashBag = new ParameterBag();
        this.context = new ParameterBag();
        this.context.add(FLASH_BAG, this.flashBag);
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

    Response redirectToRoute(String route, ParameterBag parameters) {
        return this.redirect(this.router.build(route, parameters));
    }

    Response notFound() {
        return this.response.redirect(this.router.build("@index/error", new ParameterBag().add("code", 404)));
    }

    public Response getResponse(){
        return this.response;
    }


    public void addError(String message) {
        this.addNotification(message, NotificationType.ERROR);
    }

    public void addMessage(String message) {
        this.addNotification(message, NotificationType.MESSAGE);
    }

    public void addValidation(String message) {
        this.addNotification(message, NotificationType.VALID);
    }

    public void addNotification(String message, NotificationType type) {
        this.flashBag.add(type.name(), message);
    }

}
