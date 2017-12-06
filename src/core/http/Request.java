package core.http;

import javax.servlet.http.HttpServletRequest;

public class Request {

    private HttpServletRequest request;

    private String basePath;
    private String path;

    public Request(HttpServletRequest request) {
        this.request = request;

        String path = request.getPathInfo();
        String prefix = request.getRequestURI();
        int index = prefix.indexOf(path);
        prefix = prefix.substring(0, index);

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        this.basePath = prefix;
        this.path = path;
    }

    public String getParameter(String key, String defaut){
        String value = this.request.getParameter(key);
        if(value == null) {
            return defaut;
        }
        return value;
    }

    public String getParameter(String key){
        return this.getParameter(key, null);
    }

    public String getBasePath(){
        return this.basePath;
    }

    public String getPath(){
        return path;
    }

    public HttpMethod getMethod(){
        if(this.request.getMethod() == HttpMethod.GET.name()){
            return HttpMethod.GET;
        }
        if(this.request.getMethod() == HttpMethod.POST.name()){
            return HttpMethod.POST;
        }

        return HttpMethod.GET;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
