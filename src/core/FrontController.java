package core;

import controller.IndexController;
import core.annotations.Path;
import core.annotations.Viewspace;
import core.http.*;
import core.utils.*;
import core.annotations.Route;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FrontController extends HttpServlet {

    private static Class[] controllers = new Class[]{
        IndexController.class
    };

    private static core.http.Route defaultRoute;
    static {
        try {
            defaultRoute = new core.http.Route(
                "default",
                "{code}",
                IndexController.class,
                IndexController.class.getMethod("errorAction", Request.class, Response.class, String.class),
                HttpMethod.GET
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Router router;
    private Renderer renderer;
    private Container container;

    @Override
    public void init() throws ServletException {

        this.container = new Container();
        this.router = new Router(this.container);
        this.renderer = new Renderer(this.container);

        this.renderer.addNamespace("layout", "@base/layout/");
        this.renderer.addNamespace("view", "@base/view/");
        this.renderer.addNamespace("shared", "@view/shared/");
        this.renderer.addNamespace("error", "@view/error/");

        this.container.singleton(Router.class, this.router);
        this.container.singleton(Renderer.class, this.renderer);

        for(Class controller : controllers) {
            String prefix = "";
            if(controller.isAnnotationPresent(Path.class)) {
                prefix = ((Path) controller.getAnnotation(Path.class)).value();
            }

            for(Method action : controller.getMethods()) {
                if(action.isAnnotationPresent(Route.class)) {
                    String actionName = action.getName();
                    if(!actionName.endsWith("Action")) {
                        throw new IllegalArgumentException(actionName + " must ends with 'Action'");
                    }
                    if(action.getParameterCount() < 2) {
                        throw new IllegalArgumentException(actionName + " must have at least 2 parameters : Request and Response");
                    }

                    Route annotation = action.getAnnotation(Route.class);
                    String name = annotation.name();
                    if(name.equals("__DEFAULT__")) {
                        name = actionName;
                    }
                    for(HttpMethod verbose : annotation.methods()) {
                        this.router.add(prefix + annotation.path(), controller, action, verbose, name);
                    }
                }
            }

            if(controller.isAnnotationPresent(Viewspace.class)) {
                Viewspace ns = (Viewspace) controller.getAnnotation(Viewspace.class);
                this.renderer.addNamespace(ns.namespace(), ns.path());
            }
        }

        this.router.setup();
    }

    /** Processes requests for both HTTP
     * <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {

        try {

            this.container.singleton(HttpServletRequest.class, request);
            this.container.singleton(HttpServletResponse.class, response);
            this.container.singleton(ServletContext.class, getServletContext());

            // globals
            request.setAttribute("router", this.router);
            request.setAttribute("renderer", this.renderer);

            request.setAttribute("title", request.getPathInfo());

            Request realRequest = new Request(request);
            Response realResponse = new Response(response);

            this.container.singleton(Request.class, realRequest);
            this.container.singleton(Response.class, realResponse);

            // dispatch control to view
            Match m = this.router.match(realRequest);
            if(m == null) { // no route found
                m = new Match(defaultRoute, new String[]{"404"});
            }
            this.dispatch(realRequest, realResponse, m);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dispatch(Request request, Response response, Match match)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
            InstantiationException, ServletException, IOException, ClassNotFoundException {

        /*Constructor<?> constructor = match.getRoute().getController().getDeclaredConstructor(Renderer.class, Router.class, HttpServletRequest.class);
        Object controller = constructor.newInstance(renderer, router, request);*/
        Object controller = this.container.resolve(match.getRoute().getController());
        Method action = match.getRoute().getAction();

        int numberParameters = 2 + match.getParameters().length;
        if(action.getParameterCount() != numberParameters) {
            throw new IllegalArgumentException(
                    action.getName() +
                            " must have the exact same number of parameters as declared in route path" +
                            "\nexpected " + numberParameters + " got " + action.getParameterCount()
            );
        }

        Object[] parameters = new Object[numberParameters];
        parameters[0] = request;
        parameters[1] = response;
        int i = 1;
        for (String p : match.getParameters()) {
            parameters[++i] = p;
        }

        Response res = (Response) action.invoke(controller, parameters);

        if(res.isView()) { // print
            getServletContext().getRequestDispatcher(this.renderer.render("@layout/header"))
                    .include(request.getRequest(), response.getResponse());
            getServletContext().getRequestDispatcher(response.getDestination())
                    .include(request.getRequest(), response.getResponse());
            getServletContext().getRequestDispatcher(this.renderer.render("@layout/footer"))
                    .include(request.getRequest(), response.getResponse());
        } else {  // redirect
            response.getResponse().sendRedirect(response.getDestination());
        }


    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    /** Returns a short description of the servlet */
    public String getServletInfo() {
        return "Front Controller Pattern" +
                " Servlet Front Strategy Example";
    }


}
