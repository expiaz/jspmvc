package controller;

import core.annotations.*;
import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.ParameterBag;
import core.utils.Renderer;
import entity.Etudiant;
import repository.EtudiantDAO;

@PathPrefix("/student")
@Viewspace(namespace = "student", path = "@view/student/")
public class StudentController extends BaseController {

    private EtudiantDAO dao;

    public StudentController(@Inject Renderer renderer, @Inject Router router,
                             @Inject Request request, @Inject Response response,
                             @Inject EtudiantDAO dao) {
        super(renderer, router, request, response);
        this.dao = dao;
    }

    @Route(name = "student.list", path = "/list")
    public Response listAction(Request request) {
            return this.render("@student/list",
                new ParameterBag()
                    .add("students", dao.getAll())
        );
    }

    @Route(name = "student.show", path = "/{student}")
    public Response showAction(Request request, @Parameter(name = "student", mask = "\\d+") String id) {
        return this.render("@student/show",
                new ParameterBag()
                    .add("student", dao.getById(Integer.valueOf(id)))
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

            Etudiant student = new Etudiant(nom, prenom);
            this.dao.insert(student);

            return this.redirectToRoute("student.show", new ParameterBag().add("student", student.getId()));
        }

        return this.render("@student/add", new ParameterBag().add("error", ""));
    }

    @Route(name = "student.edit", path = "/edit/{student}", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response editAction(Request request, @Parameter(name = "student", mask = "\\d+") String student) {

        int id = Integer.valueOf(student);
        Etudiant etudiant = this.dao.getById(id);

        if(request.isPost()) {

            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if(nom == null || prenom == null) {
                return this.render("@student/edit",
                        new ParameterBag()
                            .add("error", "Nom ou prénom invalide")
                            .add("student", etudiant)
                );
            }

            etudiant.setNom(nom);
            etudiant.setPrenom(prenom);
            this.dao.update(etudiant);

            return this.redirectToRoute("student.show", new ParameterBag().add("student", student));
        }

        return this.render("@student/edit",
                new ParameterBag()
                    .add("error", "")
                    .add("student", etudiant)
        );
    }

}
