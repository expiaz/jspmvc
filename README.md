# Symfonee
_The javaEE symfony based framework for lazy php developpers_

## Available commands
**These commands are available throught the symfonee utility**

Command | Parameters | Description
--------|------------|------------
_crud_ | className | generate CRUD files for className entity

## Provided abstraction (API)
_The API is quite the same as javax.ws.rs_

Below is the syntax for a Controller, a DAO and an Entity

### Controller
Controllers and actions are identified by the `@Route` tag, for a controller to be registered, you need to add it to the list at `FrontController::controllers` (it makes it easier and faster to parse classes for annotations)

The controller will be resolved and instanciated during the process of IOC, so you can DI any class in it with the `@Inject` annotation

The actions will also be resolved by the container, so `@Inject` can be performed. More powerfull, the `@Parameter` tag provide typecast and object hydratation from the parameter.
- If a non native type is awaited for a `@Parameter` parameter on an action, the resolver will look for a `@Fetchable` annotation on the provided class. If it exists, it'll call the `@Fetcher::fetch` (from `@Fetchable::from`) with the raw value of the path parameter (e.g. an id most of the time)
- If it's a native type, it'll try to cast it in the provided type
```java
@PathPrefix("/demo")
@Viewspace(namespace = "@demo", path = "@view/demo/")
public class DemoController extends BaseController{
    
    private DemoDao dao;
    
    public StudentController(@Inject Container container, @Inject DemoDAO dao) {
        super(container);
        this.dao = dao;
    }
    
    @Route(name = "demo.index", path = "/")
    public Response indexAction() {
        return this.render("@demo/index",
            new ParameterBag()
                .add("message", "Hi there")
        );
    }
    
    @Route(name = "demo.list", path = "/all")
    public Response listAction() {
        return this.render("@demo/all",
            new ParameterBag()
                .add("demos", this.dao.getAll())
        );
    }
    
    @Route(name = "demo.show", path = "/{demo}")
    public Response showAction(
        @Parameter(name = "demo", mask = "\\d+") Demo demo
    ) {
        return this.render("@demo/show",
            new ParameterBag()
                .add("demo", demo)
        );
    }
    
    @Route(
        name = "demo.edit",
        path = "/edit/{demo}",
        methods = {HttpMethod.POST, HttpMethod.GET},
        before = {AdminMiddleware.class}
    )
    public Response editAction(
        Request request,
        @Parameter(name = "demo", mask = "\\d+") Demo demo
    ) {
        
        final Response form = this.render("@demo/edit",
            new ParameterBag()
                .add("demo", demo)
        );

        if(request.isPost()) {
            String name = request.getParameter("name");
    
            if (nom == null || nom.length() == 0) {
                this.addError("Invalid name");
                return form;
            }
    
            demo.setName(name);
            this.dao.update(demo);
    
            return this.redirectToRoute("demo.show", 
                new ParameterBag()
                    .add("demo", demo.getId())
            );
        }
        
        return form;
    }
    
    @Route(name = "demo.redirect", path = "redirect")
    public Response redirectAction(Request request) {
        this.redirectToRoute("demo.index");
    }
    
}
```

#### Middleware
A middleware will be called before or after a controller and can modifiy the response or deny access.
- to modify `Request` or `Response` and continue the call use `return this.next.apply(request, response)`, you can even not return the response and modify it (so you'll mock to be the last on the chain)
- to deny access or redirect use `response.redirect(URL)` to break to call queue
```java
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
            return response.redirect(this.router.build("login"));
        }
        return this.next.apply(request, response);
    }
    
}
```

### DAO
A DAO for an DB entity only have to extend of `BaseDAO<Entity>` and basic CRUD operations will be provided
```java
public class DemoDAO extends BaseDAO<Demo> {

    public DemoDAO(@Inject EntityManager em) {
        super(em);
    }

}
```

### Entity
An entity need to extends from `BaseEntity`, it'll provide `@Fetcher` / `@Fetchable` mecanism.
An entity must have an Id.
```java
@Entity
public class Demo extends BaseEntity {

    @Id
    @GeneratedValue
	private Integer id;

    @Column(nullable = false)
	private String name;
    
    public Demo() {}
    
    public Demo(String name) {
        this.name = name;
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
```

### JSP
JSP files have access to `Router`, `Renderer`, `Container` classes to build URLS, render assets and resolve classes
```jsp
<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>
<jsp:useBean id="container" class="core.utils.Container" scope="application"/>

<jsp:useBean id="demo" class="entity.Demo" scope="request"/>

<h1>Edit a Demo</h1>

<form action="<%= router.build("demo.edit", new ParameterBag().add("demo", demo.getId())) %>" method="post">
    <input type="hidden" name="id" value="${demo.id}">
    <input type="text" name="name" value="${demo.name}" placeholder="name">
    <br/>
    <button>Edit</button>
</form>
```



## Lifecycle

### Initialisation
`FrontController::init`
- register container
- register router
- register renderer
- register database
- parse controllers for `@Viewspace`, `@PathPrefix` annotations
    - parse methods for `@Route` annotation, register routes on the router
    - register view namespace according to `@Viewspace` annotation on the renderer

### Request
`FrontController::service` -> `FrontController::processRequest`
- register `HttpServletRequest` and `HttpServletResponse` into container
- wrap the `HttpServletRequest` and `HttpServletResponse` into `Request` and `Response` and register them into container
- try to match the `javax.servlet.http.HttpServletRequest::getPathInfo` with `Router::match`
    - match each route pattern to the provided path and method (GET, POST) until finding one
- if a match was found, call `FrontController::dispatch` to start the Inversion Of Control process, else call `IndexController::errorAction` to display `HTTP 404: Not Found`
    - Create an instance of the matched route controller with `Container::resolve`, resolve each parameter of the method according to annotations
        - `@Parameter`, search to parameter in path parameters provided, resolve it according to `@Fetchable` / `@Fetcher` strategy if provided, cast by type otherwise
        - `@Inject`, resolve the parameter in the container
        - nothing: resolve the parameter from the container (as for `Request` frequently asked without `@Inject`)
    - Build the middlware stack of the route (`before -> controller -> after`) and call the first one

### Controller IOC
`DemoController::showAction`
- Do some stuff like
    - adding variables to context (e.g. `Demo demo` passed as `@Parameter`)
    - fetch things from DB with DAOs (get the refs from the container, or by DI)
- Either call `BaseController::render` with `@viewspace/jspname` (`@demo/show` here) to render JSP
- Or call `BaseController::redirectToRoute` to redirect to another URL (or `RequestDispatcher::forward`)

### View
`web/WEB-INF/view/demo/show.jsp`
- The view scope is hydrated with `Container`, `Router` and `Renderer` by default
- Other variables might be accessed either with a bean (only for classes : `<jsp:useBean id="varName" class="className" scope="request"/>`) or by JSP tags (`<% className varName = (className) request.getAttrbiute("varName"); %>`)
- JSTL is not enabled by default, you need to add `<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>` at the top of the jsp file

### Response
`FrontController::dispatch`
- execute action depending on response typen either a JSP view or URL redirection
    - JSP: `RequestDispatcher::include(header.jsp)`, `RequestDispatcher::include(Response::getDestination)`, `RequestDispatcher::include(footer.jsp)`
    - URL: `HTTPServletResponse::sendRedirect(Response::getDestination)`