package core.http;

import core.utils.ParameterBag;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private HttpServletRequest request;

    private String contextPath;
    private String path;

    private Map<String, String> overridenParameters;

    public Request(HttpServletRequest request) {
        this.request = request;

        this.overridenParameters = new HashMap<>();

        String path = request.getPathInfo();
        String prefix = request.getRequestURI();
        int index = prefix.lastIndexOf(path);
        prefix = prefix.substring(0, index);

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        this.contextPath = prefix;
        this.path = path;
    }

    public String getParameter(String key, String defaut){
        if(this.overridenParameters.containsKey(key)) {
            return this.overridenParameters.get(key);
        }

        String value = this.request.getParameter(key);
        if(value == null) {
            return defaut;
        }
        return value;
    }

    public String getParameter(String key){
        return this.getParameter(key, null);
    }

    public void setParameter(String key, String value) {
        this.overridenParameters.put(key, value);
    }

    public String getContextPath(){
        return this.contextPath;
    }

    public String getPath(){
        return path;
    }

    public HttpMethod getMethod(){
        if(this.request.getMethod().equals(HttpMethod.GET.name())){
            return HttpMethod.GET;
        }
        if(this.request.getMethod().equals(HttpMethod.POST.name())){
            return HttpMethod.POST;
        }

        return HttpMethod.GET;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public boolean isGet(){
        return this.getMethod() == HttpMethod.GET;
    }

    public boolean isPost(){
        return this.getMethod() == HttpMethod.POST;
    }
}
