package core.utils;

import java.util.HashMap;
import java.util.Map;

public class Renderer{

    private Container container;

    private static final String DF_NS = "base";

    private Map<String, String> namespaces;

    public Renderer(Container container){
        this.container = container;
        this.namespaces = new HashMap<>();
        this.namespaces.put(DF_NS, "/WEB-INF/");
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
}
