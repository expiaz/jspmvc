package core.utils;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class Renderer{

    private Container container;
    private Map<String, String> namespaces;

    public Renderer(Container container){
        this.container = container;
        this.namespaces = new HashMap<>();
    }

    public void addNamespace(String ns, String path){
        this.namespaces.put(ns, this.resolve(path));
    }

    public String render(String path) {
        path = this.resolve(path);
        if(!path.endsWith(".jsp")) {
            path += ".jsp";
        }
        return path;
    }

    public String asset(String path) {
        String ctxPath = ((ServletContext) this.container.get(ServletContext.class)).getContextPath();
        return ctxPath + this.resolve(path);
    }

    private String resolve(String path) {
        String ns;

        // '@ns/view'
        if(path.charAt(0) == '@') {
            // 'ns'
            ns = this.namespaces.get(path.substring(1, path.indexOf('/')));
            // 'view'
            path = path.substring(path.indexOf('/') + 1);

            if(path.charAt(0) == '/') {
                path = path.substring(1);
            }

            return ns + path;
        }

        return path;
    }
}
