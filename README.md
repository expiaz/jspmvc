# Symfonee
_The javaEE symfony based framework for lazy php developpers_

## Available commands
**These commands are available throught the symfonee utility**

Command | Parameters | Description
--------|------------|------------
_crud_ | className | generate CRUD files for className entity

## Provided abstraction
_The API is quite the same as javax.ws.rs_

Below is the syntax for a Controller, a DAO and an Entity

### Controller
```java
@PathPrefix("/demo")
@Viewspace(namespace = "@demo", path = "@view/demo/")
public class DemoController extends BaseController{
    
    private DemoDao dao;
    
    public StudentController(@Inject Renderer renderer, @Inject Router router,
                         @Inject Request request, @Inject Response response,
                         @Inject DemoDAO dao) {
        super(renderer, router, request, response);
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
    
    @Route(name = "demo.edit", path = "/edit/{demo}", methods = {HttpMethod.POST, HttpMethod.GET}, before = {AdminMiddleware.class})
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
```java
public class DemoDAO extends BaseDAO<Demo> {

    public DemoDAO(@Inject EntityManager em) {
        super(em);
    }

}
```

### Entity
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