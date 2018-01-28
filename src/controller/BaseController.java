package controller;

import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.annotations.Inject;
import core.utils.Container;
import core.utils.NotificationType;
import core.utils.ParameterBag;
import core.utils.Renderer;
import entity.User;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class BaseController {

    public static final String FLASH_BAG = "__FLASH__";

    /**
     * Req/Res lifecycle of the request
     */
    private Response response;
    private Request request;

    protected Container container;

    /**
     * Renderer for resolving views path
     */
    protected Renderer renderer;
    /**
     * Router for resolving routes URL
     */
    protected Router router;

    /**
     * view parameters
     */
    protected ParameterBag context;
    /**
     * special parameters that will display on the layout
     */
    private ParameterBag flashBag;

    /**
     * base constructor with injected dependencies
     * @param container
     */
    public BaseController(@Inject Container container) {
        this.container = container;
        this.renderer = (Renderer) container.get(Renderer.class);
        this.router = (Router) container.get(Router.class);
        this.request = (Request) container.get(Request.class);
        this.response = (Response) container.get(Response.class);
        this.flashBag = new ParameterBag();
        this.context = new ParameterBag();
        this.context.add(FLASH_BAG, this.flashBag);
    }

    /**
     * add the arguments to the view context and call render() (PHP MVC API Like)
     * @param viewPath
     * @param arguments
     * @return
     */
    Response render(String viewPath, ParameterBag arguments) {
        for(Map.Entry<String, Object> entry : arguments.entrySet()) {
            this.context.put(entry.getKey(), entry.getValue());
        }
        return this.render(viewPath);
    }

    /**
     * resolve the given path to the view, add context to the request and return the response
     * @param viewPath
     * @return
     */
    Response render(String viewPath) {
        for (Map.Entry<String, Object> entry : this.context.entrySet()) {
            this.request.getRequest().setAttribute(entry.getKey(), entry.getValue());
        }
        return this.response.render(this.renderer.render(viewPath));
    }

    /**
     * return the response with the redirection registered
     * @param url
     * @return
     */
    Response redirect(String url) {
        return this.response.redirect(url);
    }

    /**
     * resolve the route with the given parameters and call redirect() (PHP MVC API Like)
     * @param route
     * @param parameters
     * @return
     */
    Response redirectToRoute(String route, ParameterBag parameters) {
        return this.redirect(this.router.build(route, parameters));
    }

    Response redirectToRoute(String route) {
        return this.redirect(this.router.build(route));
    }

    // shortcut for 404
    Response notFound() {
        return this.response.redirect(this.router.build("@index/error", new ParameterBag().add("code", 404)));
    }

    /**
     * return the response object wrapper around HttpServletResponse for this request
     * @return
     */
    public Response getResponse(){
        return this.response;
    }

    /*
     * FLASHBAG
     */

    /**
     * add a notification to the flashbag, which will be displayed on the layout
     * @param message
     * @param type
     */
    private void addNotification(String message, NotificationType type) {
        this.flashBag.add(type.name(), message);
    }

    /**
     * add flashbag error
     * @param message
     */
    public void addError(String message) {
        this.addNotification(message, NotificationType.ERROR);
    }

    /**
     * add flashbag message
     * @param message
     */
    public void addMessage(String message) {
        this.addNotification(message, NotificationType.MESSAGE);
    }

    /**
     * add flashbag confirmation
     * @param message
     */
    public void addConfirmation(String message) {
        this.addNotification(message, NotificationType.VALID);
    }

    public HttpSession getSession() {
        return this.request.getRequest().getSession();
    }

    public User getUser()
    {
        return (User) this.getSession().getAttribute("user");
    }

    public boolean isLogged()
    {
        return this.getUser() != null;
    }

    public boolean isAdmin()
    {
        return this.getUser().isAdmin();
    }

}
