package middleware;

import core.http.Middleware;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.Container;
import entity.User;

import javax.servlet.http.HttpSession;

public class AdminMiddleware extends Middleware {

    Router router;

    public AdminMiddleware(Container container) {
        super(container);
        this.router = (Router) this.container.get(Router.class);
    }

    @Override
    public Response apply(Request request, Response response) {
        HttpSession session = request.getRequest().getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return response.redirect(this.router.build("index.login"));
        }
        return this.getNext().apply(request, response);
    }

}
