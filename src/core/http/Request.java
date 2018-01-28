package core.http;

import core.FrontController;
import core.annotations.Inject;
import core.utils.ParameterBag;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private HttpServletRequest request;

    private String contextPath;
    private String path;

    private Map<String, String> overridenParameters;
    private Map<String, Map<String, String>> arrayParameters;
    private Map<String, String> rawPathParameters;
    private ParameterBag pathParameters;

    public Request(@Inject HttpServletRequest request) {
        this.request = request;

        this.overridenParameters = new HashMap<>();

        String path = request.getPathInfo();
        String prefix = request.getRequestURI();
        int index = prefix.lastIndexOf(path);
        prefix = prefix.substring(0, index);

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // request.getContextPath() + request.getServletPath();

        this.contextPath = prefix;
        this.path = path;

        // parse http post variables arrays as JEE doesn't support them
        // [a[key] => value] => [a => [key => value]]
        this.arrayParameters = new HashMap<>();
        for (Map.Entry<String, String[]> parameter : request.getParameterMap().entrySet())
        {
            // note[1], note[2] .... => note -> [1 => ..., 2 => ...]

            // parameter name needs to be at least 1 char then array brace : var[] not []var
            int openBrace = 0, closeBrace = 0;
            if((openBrace = parameter.getKey().indexOf("[")) > 0)
            {
                closeBrace = parameter.getKey().indexOf("]");
                if(closeBrace <= openBrace) {
                    // no closing brace => error
                    FrontController.die(Request.class, new Exception("Error while parsing array parameters : " + parameter.getKey() + " have a '[' that doesn't end (missig ']')"));
                }
                // extract the name : var[] => var
                String name = parameter.getKey().substring(0, openBrace);
                String inBrace = parameter.getKey().substring(openBrace + 1, closeBrace);

                if(!this.arrayParameters.containsKey(name)) {
                    this.arrayParameters.put(name, new HashMap<>());
                }

                if(inBrace.isEmpty()) {
                    int i = 0;
                    for(String value : request.getParameterValues(parameter.getKey())) {
                        this.arrayParameters.get(name).put(String.valueOf(i), value);
                        ++i;
                    }
                    // inBrace = String.valueOf(transformed.get(name).size());
                } else {
                    this.arrayParameters.get(name).put(inBrace, parameter.getValue()[0]);
                }

            }
        }
    }

    public Map<String, String> getArrayParameter(String key)
    {
        if(!this.arrayParameters.containsKey(key)) {
            return new HashMap<>();
        }
        return this.arrayParameters.get(key);
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

    public void setPathParameters(ParameterBag pathParameters) {
        this.pathParameters = pathParameters;
    }

    public void setRawPathParameters(Map<String, String> rawPathParameters) {
        this.rawPathParameters = rawPathParameters;
    }

    public Object getPathParameter(String name, boolean raw) {
        if (raw) {
            if (! this.rawPathParameters.containsKey(name)) {
                return null;
            }
            return this.rawPathParameters.get(name);
        }

        if (! this.pathParameters.containsKey(name)) {
            return null;
        }
        return this.pathParameters.get(name);
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
