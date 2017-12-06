package core.http;

import javax.servlet.http.HttpServletResponse;

public class Response {

    private HttpServletResponse response;

    private String to;

    public Response(HttpServletResponse response) {
        this.response = response;
    }

    public Response render(String view) {
        this.to = view;
        return this;
    }

    public Response redirect(String path) {
        this.to = path;
        return this;
    }

    public String getDestination(){
        return to;
    }

    public boolean isRedirect(){
        return !this.isView();
    }

    public boolean isView(){
        return this.to.endsWith(".jsp");
    }

    public void header(String header, String value){
        this.response.addHeader(header, value);
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
