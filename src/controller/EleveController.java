package controller;

import core.http.Router;
import core.utils.Renderer;

import javax.servlet.http.HttpServletRequest;

public class EleveController extends BaseController {

    protected EleveController(Renderer renderer, Router router, HttpServletRequest request) {
        super(renderer, router, request);
    }

}
