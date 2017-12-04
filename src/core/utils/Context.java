package core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Context {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String pathInfo;
    private String requestUri;

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Context(HttpServletRequest request, HttpServletResponse response, String pathInfo, String requestUri) {
        this.request = request;
        this.response = response;
        this.pathInfo = pathInfo;
        this.requestUri = requestUri;
    }

}

