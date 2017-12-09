package controller;

import core.annotations.*;
import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.ParameterBag;
import core.utils.Renderer;
import entity.Etudiant;
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
        return this.render("@student/list",
                new ParameterBag()
                    .add("students", GestionFactory.getEtudiants())
        );
    }

    @Route(name = "student.show", path = "/{student}")
    public Response showAction(Request request, @Argument(name = "student", mask = "\\d+") String id) {
        return this.render("@student/show",
                new ParameterBag()
                    .add("student", GestionFactory.getEtudiantById(Integer.valueOf(id)))
        );
    }

    @Route(name = "student.add", path = "/add", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response addAction(Request request) {

        if(request.isPost()) {
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if(nom == null || prenom == null) {
                return this.render("@student/add",
                        new ParameterBag()
                                .add("error", "Nom ou prénom invalide")
                );
            }

            Etudiant e = GestionFactory.create(nom, prenom);
            GestionFactory.save(e);

            return this.redirect(this.router.build("student.show", new ParameterBag().add("student", e.getId())));
        }

        return this.render("@student/add", new ParameterBag().add("error", ""));
    }

    @Route(name = "student.edit", path = "/edit/{student}", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response editAction(Request request, @Argument(name = "student", mask = "\\d+") String student) {

        Etudiant e;
        try {
            int id = Integer.valueOf(student);
            e = GestionFactory.getEtudiantById(id);
        } catch (Exception ex) {
            return this.notFound();
        }

        if(request.isPost()) {

            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if(nom == null || prenom == null) {
                return this.render("@student/edit",
                        new ParameterBag()
                            .add("error", "Nom ou prénom invalide")
                );
            }

            e.setNom(nom);
            e.setPrenom(prenom);
            GestionFactory.save(e);

            return this.redirect(this.router.build("student.show", new ParameterBag().add("student", student)));
        }

        return this.render("@student/edit",
                new ParameterBag()
                    .add("error", "")
                    .add("student", e)
        );
    }

}
