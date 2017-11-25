package core;

import core.http.Match;
import core.http.Router;
import core.utils.Renderer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class FrontController extends HttpServlet {

    private Router router;
    private Renderer renderer;

    @Override
    public void init() throws ServletException {
        this.router = new Router();
        this.renderer = new Renderer();

        this.router.get("/hello/(\\w+)", controller.IndexController.class, "hello", "base.hello");


        this.renderer.addNamespace("layout", "@base/layout/");
        this.renderer.addNamespace("view", "@base/view/");

        this.renderer.addNamespace("shared", "@view/shared/");

        this.renderer.addNamespace("hello", "@view/hello/");
    }

    /** Processes requests for both HTTP
     * <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {

        try {
            // globals
            request.setAttribute(Router.class.getName(), this.router);
            request.setAttribute(Renderer.class.getName(), this.renderer);

            String t = request.getPathInfo();

            // dispatch control to view
            Match m = this.router.match(request);
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

        Constructor<?> constructor = match.getRoute().getController().getDeclaredConstructor(Renderer.class, HttpServletRequest.class);
        Object controller = constructor.newInstance(renderer, request);
        Method action = controller.getClass().getDeclaredMethod(match.getRoute().getAction() + "Action", HttpServletRequest.class, HttpServletResponse.class, List.class);

        String view = (String) action.invoke(controller, request, response, match.getParameters());

        getServletContext().getRequestDispatcher(view).forward(request, response);
    }
}
