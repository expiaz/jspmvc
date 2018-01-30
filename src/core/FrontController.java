package core;

import controller.*;
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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FrontController extends HttpServlet {

    public static void die(Class from, Exception why) {
        System.out.println("\nFrontController::die : unrecoverable error from " + from.getName() + "\n" + why.getMessage());
        why.printStackTrace();
        instance.destroy();
        System.exit(1);
    }

    private static Class<? extends BaseController>[] controllers = new Class[]{
        IndexController.class,
        EtudiantController.class,
        ModuleController.class,
        NoteController.class
    };

    private static core.http.Route defaultRoute;
    static {
        try {
            defaultRoute = new core.http.Route(
                "default",
                "{code}",
                IndexController.class,
                IndexController.class.getMethod("errorAction", int.class),
                HttpMethod.GET,
                new ArrayList<>(),
                new ArrayList<>()
            );
        } catch (NoSuchMethodException e) {
            FrontController.die(FrontController.class, e);
        }
    }

    private static FrontController instance = null;
    public static boolean DEV = false;

    private Router router;
    private Renderer renderer;
    private Container container;
    private Database database;

    @Override
    public void init() throws ServletException {

        instance = this;

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
        this.container.singleton(ServletContext.class, getServletContext());

        // globals
        getServletContext().setAttribute("router", this.router);
        getServletContext().setAttribute("renderer", this.renderer);
        getServletContext().setAttribute("container", this.container);

        this.container.factory(EntityManager.class, new Factory<EntityManager>() {
            @Override
            public EntityManager create(Container container) {
                return ((Database) container.get(Database.class)).getEntityManager();
            }
        });

        for(Class<? extends BaseController> controller : controllers) {

            if(! controller.getName().endsWith("Controller")) {
                FrontController.die(FrontController.class, new IllegalArgumentException(controller.getName() + " must ends with 'Controller'"));
            }

            String prefix = "";
            if(controller.isAnnotationPresent(PathPrefix.class)) {
                prefix = ((PathPrefix) controller.getAnnotation(PathPrefix.class)).value();
            }

            for(Method action : controller.getMethods()) {
                if(action.isAnnotationPresent(Route.class)) {
                    String actionName = action.getName();

                    if(!actionName.endsWith("Action")) {
                        FrontController.die(FrontController.class, new IllegalArgumentException(actionName + " must ends with 'Action'"));
                    }
                    if(action.getReturnType() != Response.class) {
                        FrontController.die(FrontController.class, new IllegalArgumentException(actionName + " must return a Response, " + action.getReturnType().getName() + " returned"));
                    }

                    Route annotation = action.getAnnotation(Route.class);
                    String name = annotation.name();
                    if(name.equals("__DEFAULT__")) {
                        name = actionName;
                    }
                    for(HttpMethod verbose : annotation.methods()) {
                        this.router.add(
                            prefix + annotation.path(),
                            controller, action, verbose, name,
                            annotation.before(), annotation.after()
                        );
                    }
                }
            }

            if(controller.isAnnotationPresent(Viewspace.class)) {
                Viewspace ns = (Viewspace) controller.getAnnotation(Viewspace.class);
                this.renderer.addNamespace(ns.namespace(), ns.path());
            }
        }

        // sort routes for matching
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

        this.container.singleton(HttpServletRequest.class, request);
        this.container.singleton(HttpServletResponse.class, response);

        Request realRequest = new Request(request);
        Response realResponse = new Response(response);

        this.container.singleton(Request.class, realRequest);
        this.container.singleton(Response.class, realResponse);

        try {
            response.setCharacterEncoding("UTF-8");
            request.setAttribute("title", request.getPathInfo());

            // dispatch control to view
            Match m = this.router.match(realRequest);
            if(m == null) { // no route found
                throw new Exception();
            }
            this.dispatch(realRequest, realResponse, m);

        } catch (Exception e){
            if (DEV) {
                FrontController.die(FrontController.class, e);
            }
            try {
                // dispatch to default route (404)
                this.dispatch(
                    realRequest,
                    realResponse,
                    new Match(defaultRoute, new ParameterBag().add("code", "404"))
                );
            } catch (Exception e2) {
                FrontController.die(FrontController.class, e2);
            }
        }
    }

    private void dispatch(Request request, Response response, final Match match)
            throws InvalidParameterException, InvocationTargetException, IllegalAccessException,
            NoSuchMethodException, InstantiationException, ServletException,
            IOException, ClassNotFoundException {

        // resolve the controller with the Container Resolver
        final Object controller = this.container.resolve(match.getRoute().getController());
        // parse the action arguments
        final Method action = match.getRoute().getAction();

        // retrieve parameters
        Object[] parameters = new Object[action.getParameterCount()];
        ParameterBag resolvedParameters = new ParameterBag();
        Map<String, String> rawParameters = new HashMap<>();
        int i = 0;
        for(java.lang.reflect.Parameter p : action.getParameters()) {
            if (p.isAnnotationPresent(Parameter.class)) { // annotation found for the specified parameter
                Parameter pannotation;
                Class parameterClass = p.getType();
                Object param;
                String rawParam;

                pannotation = p.getAnnotation(Parameter.class);
                rawParam = match.getParameters().get(pannotation.name()).toString();

                // is fetchable present on the parameter type ? (it define the strategy to retrieve the given parameter)
                if(Resolver.isInterfacePresent(Fetchable.class, parameterClass)) { // Fetchable present
                    Fetchable fetchable = (Fetchable) parameterClass.newInstance();
                    Fetcher fetcher = (Fetcher) this.container.get(fetchable.from());
                    param = fetcher.fetch(rawParam);
                    if(param == null) {
                        throw new InvalidParameterException(
                            "can't dispatch to action " + controller.getClass().getName() + "::" +
                            action.getName() + " : parameter " + pannotation.name() + " with provided value of " + rawParam +
                            " for type " + parameterClass.getName()
                        );
                        /*
                        FrontController.die(
                            FrontController.class,
                            new InvalidParameterException(
                                "can't dispatch to action " + controller.getClass().getName() + "::" +
                                action.getName() + " : parameter " + pannotation.name() + " with provided value of " + rawParam +
                                " for type " + parameterClass.getName()
                            )
                        );
                         */
                    }
                } else { // no fetching strategy provided, just retrieve it from the raw matched ones and cast it
                    Object casted = null;
                    if(parameterClass.getName().equals("int") || parameterClass.getName().equals(Integer.class.getName())) {
                        casted = Integer.valueOf(rawParam);
                    } else if(parameterClass.getName().equals("float") || parameterClass.getName().equals(Float.class.getName())) {
                        casted = Float.valueOf(rawParam);
                    } else if (parameterClass.getName().equals("double") || parameterClass.getName().equals(Double.class.getName())) {
                        casted = Double.valueOf(rawParam);
                    } else if (parameterClass.getName().equals(Short.class.getName()) || parameterClass.getName().equals("short")) {
                        casted = Short.valueOf(rawParam);
                    } else if (parameterClass.getName().equals("byte") || parameterClass.getName().equals(Byte.class.getName())) {
                        casted = Byte.valueOf(rawParam);
                    } else if (parameterClass.getName().equals("boolean") || parameterClass.getName().equals(Boolean.class.getName())) {
                        casted = rawParam.toLowerCase().equals("true") || rawParam.toLowerCase().equals("1");
                    } else if ( parameterClass.getName().equals(String.class.getName()) ) {
                        casted = rawParam;
                    } else {
                        throw new InvalidParameterException(rawParam + " is not a possible value for type " + parameterClass.getName());
                        /*
                        FrontController.die(
                            FrontController.class,
                            new InvalidParameterException(rawParam + " is not a possible value for type " + parameterClass.getName())
                        );
                         */
                    }
                    param = casted;
                }
                // add it to the list
                resolvedParameters.put(pannotation.name(), param);
                rawParameters.put(pannotation.name(), rawParam);
                parameters[i++] = param;
            } else if (p.isAnnotationPresent(Inject.class)) { // Dependency injection asked
                // get details for DI
                Inject pinject = p.getAnnotation(Inject.class);
                // resolve the parameter and add it to the list
                parameters[i++] = this.container.get(
                    pinject.key().equals("__DEFAULT__")
                            ? p.getType().getName()
                            : pinject.key(),
                    pinject.factory()
                );
            } else { // nothing found, try to resolve the type with the container (it'll surely fail)
                parameters[i++] = this.container.get(p.getType().getName());
            }


        }

        request.setPathParameters(resolvedParameters);
        request.setRawPathParameters(rawParameters);

        Middleware controllerMiddleware = new Middleware(this.container) {
            @Override
            public Response apply(Request request, Response response) {
                try {
                    Response r = (Response) action.invoke(controller, parameters);
                    return this.getNext().apply(request, r);
                } catch (Exception e) {
                    FrontController.die(match.getRoute().getController(), new Exception("dispatch of " + request.getMethod() + " " + action.getName() + " failed : \n " + e.getMessage()));
                    return null;
                }
            }
        };

        // IOC and get response
        Response res = match.getRoute().getMiddlewareStack(this.container, controllerMiddleware).apply(request, response);

        // switch over response type to handle it correctly, either print or redirection
        if(res.isView()) { // print
            // layout
            getServletContext().getRequestDispatcher(this.renderer.render("@layout/header"))
                .include(request.getRequest(), response.getResponse());
            // view
            getServletContext().getRequestDispatcher(response.getDestination())
                .include(request.getRequest(), response.getResponse());
            // layout
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
        return "Front Controller Pattern Servlet Front Strategy Example";
    }


}
