package middleware;

import core.http.Middleware;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.Container;
import core.utils.ParameterBag;
import entity.User;

import javax.servlet.http.HttpSession;

public class EtudiantMiddleware extends Middleware {

    Router router;

    public EtudiantMiddleware(Container container) {
        super(container);
        this.router = (Router) this.container.get(Router.class);
    }

    @Override
    public Response apply(Request request, Response response) {
        HttpSession session = request.getRequest().getSession();
        User user = (User) session.getAttribute("user");
        User requestedUser = (User) request.getPathParameter("etudiant", false);

        if (user == null) {
            return response.redirect(this.router.build("index.login"));
        }

        if ((requestedUser == null || user.getId() != requestedUser.getId()) && !user.isAdmin()) {
            return response.redirect(this.router.build("etudiant.show", new ParameterBag().add("etudiant", user.getId())));
        }

        return this.getNext().apply(request, response);
    }

}
