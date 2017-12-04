package core;

import controller.IndexController;
import core.http.HttpMethod;
import core.http.Match;
import core.http.Router;
import core.utils.Context;
import core.utils.Renderer;
import core.utils.Route;
import core.utils.Viewspace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FrontController extends HttpServlet {

    private static Class[] controllers = new Class[]{
        IndexController.class
    };

    private static core.http.Route defaultRoute;
    static {
        try {
            defaultRoute = new core.http.Route(
                "default",
                ".*",
                IndexController.class,
                IndexController.class.getMethod("errorAction", HttpServletRequest.class, HttpServletResponse.class),
                HttpMethod.GET
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Router router;
    private Renderer renderer;

    @Override
    public void init() throws ServletException {

        this.router = new Router();
        this.renderer = new Renderer();

        this.renderer.addNamespace("layout", "@base/layout/");
        this.renderer.addNamespace("view", "@base/view/");
        this.renderer.addNamespace("shared", "@view/shared/");

        for(Class controller : controllers) {
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
                        this.router.add(annotation.path(), controller, action, verbose, name);
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {

        try {
            // setup contexts
            this.router.setContext(new Context(request, response, request.getPathInfo(), request.getRequestURI()));
            this.renderer.setContext(new Context(request, response, request.getPathInfo(), request.getRequestURI()));

            // globals
            request.setAttribute("router", this.router);
            request.setAttribute("renderer", this.renderer);

            // dispatch control to view
            Match m = this.router.match(request);
            if(m == null) { // no route found
                m = new Match(defaultRoute, new ArrayList<>());
            }
            this.dispatch(request, response, m);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        this.processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        this.processRequest(request, response);
    }

    /** Returns a short description of the servlet */
    public String getServletInfo() {
        return "Front Controller Pattern" +
                " Servlet Front Strategy Example";
    }

    protected void dispatch(HttpServletRequest request, HttpServletResponse response, Match match)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ServletException, IOException {

        Constructor<?> constructor = match.getRoute().getController().getDeclaredConstructor(Renderer.class, Router.class, HttpServletRequest.class);
        Object controller = constructor.newInstance(renderer, router, request);
        Method action = match.getRoute().getAction();

        int numberParameters = 2 + match.getParameters().size();
        if(action.getParameterCount() != numberParameters) {
            throw new IllegalArgumentException(
                action.getName() +
                " must have the exact same number of parameters as declared in route path" +
                "\nexpected " + numberParameters + " got " + action.getParameterCount()
            );
        }

        Object[] parameters = new Object[2 + match.getParameters().size()];
        parameters[0] = request;
        parameters[1] = response;
        int i = 1;
        for (String p : match.getParameters()) {
            parameters[++i] = p;
        }

        String view = (String) action.invoke(controller, parameters);

        // redirect
        if(view.equals("__REDIRECT__")) {
            return;
        }

        getServletContext().getRequestDispatcher(this.renderer.render("@layout/header")).include(request, response);

        getServletContext().getRequestDispatcher(view).include(request, response);

        getServletContext().getRequestDispatcher(this.renderer.render("@layout/footer")).include(request, response);
    }
}
