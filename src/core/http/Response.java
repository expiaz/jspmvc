package core.http;

import core.annotations.Inject;

import javax.servlet.http.HttpServletResponse;

/**
 * wrapper around HttpServletResponse to expose nicer API
 */
public class Response {

    /**
     * real response
     */
    private HttpServletResponse response;

    /**
     * URL to which points
     */
    private String to;

    public Response(@Inject HttpServletResponse response) {
        this.response = response;
    }

    /**
     * request will be a view
     * @param view
     * @return
     */
    public Response render(String view) {
        this.to = view;
        return this;
    }

    /**
     * request will be a redirection
     * @param path
     * @return
     */
    public Response redirect(String path) {
        this.to = path;
        return this;
    }

    /**
     * get the url to which the request points to
     * @return
     */
    public String getDestination(){
        return to;
    }

    /**
     * is the request a redirection
     * @return
     */
    public boolean isRedirect(){
        return !this.isView();
    }

    /**
     * is the request a view rendering
     * @return
     */
    public boolean isView(){
        return this.to.endsWith(".jsp");
    }

    /**
     * add a header field to http response
     * @param header
     * @param value
     */
    public void header(String header, String value){
        this.response.addHeader(header, value);
    }

    /**
     * return real response
     * @return
     */
    public HttpServletResponse getResponse() {
        return response;
    }
}
