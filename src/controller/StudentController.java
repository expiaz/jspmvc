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

    @Route(name = "student.list", path = "/all")
    public Response listAction() {
            return this.render("@student/list",
                new ParameterBag()
                    .add("students", dao.getAll())
        );
    }

    @Route(name = "student.show", path = "/{student}")
    public Response showAction(@Parameter(name = "student", mask = "\\d+") Etudiant student) {
        return this.render("@student/show",
                new ParameterBag()
                    .add("student", student)
        );
    }

    @Route(name = "student.add", path = "/add", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response addAction(Request request) {

        if(request.isPost()) {
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if (nom == null || prenom == null
                || nom.length() == 0 || prenom.length() == 0
            ) {
                this.addError("Nom ou prénom invalide");
                return this.render("@student/add");
            }

            Etudiant student = new Etudiant(nom, prenom);
            this.dao.insert(student);

            return this.redirectToRoute("student.show", new ParameterBag().add("student", student.getId()));
        }

        return this.render("@student/add");
    }

    @Route(name = "student.edit", path = "/edit/{student}", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response editAction(Request request,
                               @Parameter(name = "student", mask = "\\d+") Etudiant student) {

        if(request.isPost()) {

            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if (nom == null || prenom == null
                || nom.length() == 0 || prenom.length() == 0
            ) {
                this.addError("Nom ou prénom invalide");
                return this.render("@student/edit",
                        new ParameterBag()
                            .add("student", student)
                );
            }

            student.setNom(nom);
            student.setPrenom(prenom);
            this.dao.update(student);

            return this.redirectToRoute("student.show", new ParameterBag().add("student", student.getId()));
        }

        return this.render("@student/edit",
                new ParameterBag()
                    .add("student", student)
        );
    }

}
