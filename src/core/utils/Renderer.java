package core.utils;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Renderer{

    private Container container;
    private Map<String, String> namespaces;

    public Renderer(Container container){
        this.container = container;
        this.namespaces = new HashMap<>();
    }

    /**
     * add a shortcut for a path
     * @param ns the shortcut notation e.g 'view'
     * @param path the path resolved from web e.g 'path/to/folder/' it can contains other ns
     */
    public void addNamespace(String ns, String path){
        this.namespaces.put(ns, this.resolve(path));
    }

    /**
     * get the path from a registered shortcut
     * @param path
     * @return
     */
    public String render(String path) {
        path = this.resolve(path);
        // view path should ends with jsp
        if(!path.endsWith(".jsp")) {
            path += ".jsp";
        }
        return path;
    }

    /**
     * get the web path to the web folder and add the resolved shortcut path to it
     * @param path
     * @return
     */
    public String asset(String path) {
        // get the context path (e.g. X_WAR_EXPLODED)
        String ctxPath = ((ServletContext) this.container.get(ServletContext.class)).getContextPath();
        // return the path to the asked file
        return ctxPath + this.resolve(path) + "?v=" + UUID.randomUUID();
    }

    /**
     * transform the shortcut path into it's associed path given with addNamespace
     * @param path
     * @return
     */
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
