package core;

import controller.GroupeController;
import controller.IndexController;
import controller.StudentController;
import core.annotations.*;
import core.annotations.Route;
import core.database.Database;
import core.http.*;
import core.utils.*;

import javax.persistence.EntityManager;
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
        IndexController.class,
        StudentController.class,
        GroupeController.class
    };

    private static core.http.Route defaultRoute;
    static {
        try {
            defaultRoute = new core.http.Route(
                "default",
                "{code}",
                IndexController.class,
                IndexController.class.getMethod("errorAction", Integer.class),
                HttpMethod.GET
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Router router;
    private Renderer renderer;
    private Container container;
    private Database database;

    @Override
    public void init() throws ServletException {

        this.container = new Container();
        this.router = new Router(this.container);
        this.renderer = new Renderer(this.container);
        this.database = Database.getInstance();

        this.renderer.addNamespace("webinf", "/WEB-INF/");
        this.renderer.addNamespace("view", "@webinf/view/");
        this.renderer.addNamespace("layout", "@view/layout/");
        this.renderer.addNamespace("shared", "@view/shared/");
        this.renderer.addNamespace("error", "@view/error/");

        this.renderer.addNamespace("assets", "/assets/");
        this.renderer.addNamespace("css", "@assets/css/");
        this.renderer.addNamespace("js", "@assets/js/");
        this.renderer.addNamespace("img", "@assets/img/");

        this.container.global(this.router);
        this.container.global(this.renderer);
        this.container.global(this.database);
        this.container.factory(EntityManager.class, new Factory() {
            @Override
            public Object create(Container container) {
                return ((Database) container.get(Database.class)).getEntityManager();
            }
        });

        for(Class controller : controllers) {

            if(! controller.getName().endsWith("Controller")) {
                throw new IllegalArgumentException(controller.getName() + " must ends with 'Controller'");
            }

            String prefix = "";
            if(controller.isAnnotationPresent(PathPrefix.class)) {
                prefix = ((PathPrefix) controller.getAnnotation(PathPrefix.class)).value();
            }

            for(Method action : controller.getMethods()) {
                if(action.isAnnotationPresent(Route.class)) {
                    String actionName = action.getName();
                    if(!actionName.endsWith("Action")) {
                        throw new IllegalArgumentException(actionName + " must ends with 'Action'");
                    }
/*                    if(action.getParameterCount() < 1) {
                        throw new IllegalArgumentException(actionName + " must have at least 1 parameter : Request");
                    }
                    if(action.getParameters()[0].getType() != Request.class) {
                        throw new IllegalArgumentException(" first parameter of " + actionName + " must be Request");
                    }*/
                    if(action.getReturnType() != Response.class) {
                        throw new IllegalArgumentException(actionName + " must return a Response, " + action.getReturnType().getName() + " returned");
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

    @Override
    public void destroy() {
        super.destroy();
        this.database.destroy();
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

            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");

            // globals
            request.setAttribute("router", this.router);
            request.setAttribute("renderer", this.renderer);
            request.setAttribute("container", this.container);

            request.setAttribute("title", request.getPathInfo());

            Request realRequest = new Request(request);
            Response realResponse = new Response(response);

            this.container.singleton(Request.class, realRequest);
            this.container.singleton(Response.class, realResponse);

            // dispatch control to view
            Match m = this.router.match(realRequest);
            if(m == null) { // no route found
                m = new Match(defaultRoute, new ParameterBag().add("code", "404"));
            }
            this.dispatch(realRequest, realResponse, m);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dispatch(Request request, Response response, Match match)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
            InstantiationException, ServletException, IOException, ClassNotFoundException {

        Class v = Class.forName("org.sqlite.JDBC");

        /*Constructor<?> constructor = match.getRoute().getController().getDeclaredConstructor(Renderer.class, Router.class, HttpServletRequest.class);
        Object controller = constructor.newInstance(renderer, router, request);*/
        Object controller = this.container.resolve(match.getRoute().getController());
        Method action = match.getRoute().getAction();

        /*int numberParameters = 1 + match.getParameters().length;
        if(action.getParameterCount() != numberParameters) {
            throw new IllegalArgumentException(
                    action.getName() +
                            " must have the exact same number of parameters as declared in route path" +
                            "\nexpected " + numberParameters + " got " + action.getParameterCount()
            );
        }*/

        Object[] parameters = new Object[action.getParameterCount()];
        int i = 0;
        for(java.lang.reflect.Parameter p : action.getParameters()) {
            if (p.isAnnotationPresent(Parameter.class)) {
                Parameter pannotation = (Parameter) p.getAnnotation(Parameter.class);

                Class parameterClass = p.getType();
                Fetchable fetchable;
                Fetcher fetcher;
                Object param;

                if(parameterClass.isAnnotationPresent(Fetchable.class)) {
                    fetchable = (Fetchable) parameterClass.getAnnotation(Fetchable.class);
                    fetcher = (Fetcher) this.container.get(fetchable.from());
                    param = fetcher.fetch(match.getParameters().get(pannotation.name()));
                } else if(Resolver.isInterfacePresent(Fetchable.class, parameterClass)) {
                    fetchable = (Fetchable) parameterClass.newInstance();
                    fetcher = (Fetcher) this.container.get(fetchable.from());
                    param = fetcher.fetch(match.getParameters().get(pannotation.name()));
                } else {
                    param = match.getParameters().get(pannotation.name());
                }

                parameters[i++] = param;
            } else if (p.isAnnotationPresent(Inject.class)) {
                Inject pinject = p.getAnnotation(Inject.class);
                parameters[i++] = this.container.get(
                        pinject.key().equals("__DEFAULT__")
                                ? p.getType().getName()
                                : pinject.key(),
                        pinject.factory()
                );
            } else {
                parameters[i++] = this.container.get(p.getType().getName());
            }
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
