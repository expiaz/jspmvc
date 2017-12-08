package controller;

import core.annotations.*;
import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.http.Router;
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
        this.context.put("students", GestionFactory.getEtudiants());
        return this.render("@student/list");
    }

    @Route(name = "student.show", path = "/{student}")
    public Response showAction(Request request, @Argument(name = "student", mask = "\\d+") String id) {
        this.context.put("student", GestionFactory.getEtudiantById(Integer.valueOf(id)));
        return this.render("@student/show");
    }

    @Route(name = "student.add", path = "/add", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response addAction(Request request) {

        if(request.isPost()) {
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if(nom == null || prenom == null) {
                this.context.put("error", "Nom ou prénom invalide");
                return this.render("@student/add");
            }

            Etudiant e = GestionFactory.create(nom, prenom);
            GestionFactory.save(e);

            return this.redirect(this.router.build("student.show", new String[][] {
                    new String[] {"student", e.getId().toString()}
            }));
        }

        this.context.put("error", "");
        return this.render("@student/add");
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
                this.context.put("error", "Nom ou prénom invalide");
                return this.render("@student/edit");
            }

            e.setNom(nom);
            e.setPrenom(prenom);
            GestionFactory.save(e);

            return this.redirect(this.router.build("student.show", new String[][] {
                new String[] {"student", student}
            }));
        }

        this.context.put("error", "");
        this.context.put("student", e);
        return this.render("@student/edit");
    }

}
