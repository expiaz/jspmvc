package controller;

import core.annotations.Parameter;
import core.annotations.Inject;
import core.annotations.Route;

import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.utils.Container;
import core.utils.ParameterBag;
import entity.Admin;
import entity.User;
import repository.AdminDAO;

public class IndexController extends BaseController {

    public IndexController(@Inject Container container) {
        super(container);
    }

    @Route(name = "index.home")
    public Response indexAction() {
        AdminDAO dao = ((AdminDAO) this.container.get(AdminDAO.class));
        if (!dao.exists("root")) {
            dao.insert(new Admin("root", "root"));
        }

        return this.render("@view/home");
    }

    @Route(name = "index.error", path = "/error/{code}")
    public Response errorAction(@Parameter(mask = "\\d{3}", name = "code") int error) {
        return this.render("@error/404",
                new ParameterBag()
                    .add("code", error)
        );
    }

    @Route(name = "index.login", path = "/login", methods = {HttpMethod.GET, HttpMethod.POST})
    public Response loginAction(Request request) {

        Response form = this.render("@shared/login");

        if (this.isLogged()) {
            return this.redirectToRoute("index.home");
        }

        if(request.isPost()) {

            String login = request.getParameter("login", "");
            String password = request.getParameter("password", "");

            AdminDAO adminDAO = (AdminDAO) this.container.get(AdminDAO.class);

            User user = adminDAO.authenticate(login, password);
            if(user == null) {
                this.addError("Login ou mot de passe incorrect");
                return form;
            }

            this.getSession().setAttribute("user", user);
            return this.redirectToRoute("index.home");
        }

        return form;
    }

    @Route(name = "index.logout", path = "/logout")
    public Response logoutAction() {

        if(!this.isLogged()) {
            return this.redirectToRoute("index.login");
        }

        this.getSession().removeAttribute("user");
        return this.redirectToRoute("index.home");
    }

}
