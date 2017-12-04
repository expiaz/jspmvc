package core.utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Renderer implements Contextual{

    private Context currentContext;

    private static final String DF_NS = "base";

    private Map<String, String> namespaces;

    public Renderer(){
        this.namespaces = new HashMap<>();
        this.namespaces.put(DF_NS, "/WEB-INF/");
    }

    public void addNamespace(String ns, String path){
        this.namespaces.put(ns, this.resolve(path));
    }

    public void include(String view) {
        try {
            this.currentContext.getRequest().getRequestDispatcher(this.render(view))
                    .include(this.currentContext.getRequest(), this.currentContext.getResponse());
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String render(String path) {
        path = this.resolve(path);
        if(path.length() < 5 || path.substring(path.length() - 4) != ".jsp") {
            path += ".jsp";
        }
        return path;
    }

    private String resolve(String path) {
        String ns;

        // '@ns/view'
        if(path.charAt(0) == '@') {
            // 'ns'
            ns = this.namespaces.get(path.substring(1, path.indexOf('/')));
            // 'view'
            path = path.substring(path.indexOf('/') + 1);
        } else {
            ns = this.namespaces.get(DF_NS);
        }

        if(path.charAt(0) == '/') {
            path = path.substring(1);
        }

        return ns + path;
    }

    @Override
    public void setContext(Context context) {
        this.currentContext = context;
    }
}
