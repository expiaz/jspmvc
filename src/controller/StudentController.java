package controller;

import core.annotations.*;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.Renderer;
import factory.GestionFactory;

@Path("/student")
@Viewspace(namespace = "student", path = "@view/student/")
public class StudentController extends BaseController {

    public StudentController(@Inject Renderer renderer, @Inject Router router,
                      @Inject Request request, @Inject Response response) {
        super(renderer, router, request, response);
    }

    @Route(name = "student.list", path = "/list")
    public Response listAction(Request request) {
        this.context.put("students", GestionFactory.getEtudiants());
        return this.render("@student/list");
    }

    @Route(name = "student.show", path = "/{student}")
    public Response showAction(Request request, @Argument(name = "student", mask = "\\d+") String id) {
        this.context.put("student", GestionFactory.getEtudiantById(Integer.valueOf(id)));
        return this.render("@student/show");
    }

}
