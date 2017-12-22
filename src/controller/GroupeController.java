package controller;

import core.annotations.*;
import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.ParameterBag;
import core.utils.Renderer;
import entity.Etudiant;
import entity.Groupe;
import repository.GroupeDAO;

@PathPrefix("/group")
@Viewspace(namespace = "@group", path = "@view/group/")
public class GroupeController extends BaseController{

    private GroupeDAO dao;

    public GroupeController(@Inject Renderer renderer, @Inject Router router,
                            @Inject Request request, @Inject Response response,
                            @Inject GroupeDAO dao) {
        super(renderer, router, request, response);
        this.dao = dao;
    }

    @Route(name = "group.list", path = "/all")
    public Response listAction(Request request) {
        return this.render("@group/list",
                new ParameterBag()
                    .add("groups", dao.getAll())
        );
    }

    @Route(name = "group.show", path = "/{group}")
    public Response showAction(@Parameter(name = "group", mask = "\\d+") int id) {
        return this.render("@student/show",
                new ParameterBag()
                    .add("group", dao.getById(Integer.valueOf(id)))
        );
    }

    @Route(name = "group.add", path = "/add", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response addAction(Request request) {

        if(request.isPost()) {
            String nom = request.getParameter("nom");

            if (nom == null || nom.length() == 0) {
                this.addError("Nom invalide");
                return this.render("@group/add");
            }

            Groupe group = new Groupe(nom);
            this.dao.insert(group);

            return this.redirectToRoute("group.show", new ParameterBag().add("group", group.getId()));
        }

        return this.render("@group/add");
    }

    @Route(name = "group.edit", path = "/edit/{group}", methods = {HttpMethod.POST, HttpMethod.GET})
    public Response editAction(Request request, @Parameter(name = "group", mask = "\\d+") int group) {

        int id = Integer.valueOf(group);
        Groupe groupe = this.dao.getById(id);

        if(request.isPost()) {
            String nom = request.getParameter("nom");

            if (nom == null || nom.length() == 0) {
                this.addError("Nom invalide");
                return this.render("@group/edit",
                        new ParameterBag().add("group", groupe)
                );
            }

            groupe.setNom(nom);
            this.dao.update(groupe);

            return this.redirectToRoute("group.show", new ParameterBag().add("group", groupe));
        }

        return this.render("@student/edit",
                new ParameterBag().add("group", groupe)
        );
    }

}
