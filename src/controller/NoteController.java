package controller;

import core.annotations.*;
import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.http.Router;
import core.utils.Container;
import core.utils.ParameterBag;
import core.utils.Renderer;

import entity.Etudiant;
import entity.Note;
import middleware.AdminMiddleware;
import repository.NoteDAO;
import service.NoteService;

@PathPrefix("/note")
@Viewspace(namespace = "note", path = "@view/note/")
public class NoteController extends BaseController {

    private NoteDAO dao;

    public NoteController(@Inject Container container, @Inject NoteDAO dao) {
        super(container);
        this.dao = dao;
    }

    @Route(name = "note.edit", path = "/edit/{note}", methods = {HttpMethod.POST, HttpMethod.GET}, before = AdminMiddleware.class)
    public Response editAction(Request request,
                               @Parameter(name = "note", mask = "\\d+") Note note) {

        Response form = this.render("@note/edit",
            new ParameterBag().add("note", note)
        );

        NoteService noteService = (NoteService) this.container.get(NoteService.class);

        if(request.isPost()) {
            float valeur;
            try {
                valeur = noteService.isNoteValid(request.getParameter("note"), true); /*Float.parseFloat(request.getParameter("note"));*/
            } catch (Exception e) {
                this.addError("Note invalide");
                return form;
            }

            note.setValeur(valeur);
            this.dao.update(note);

            return this.redirectToRoute("etudiant.show",
                new ParameterBag().add("etudiant", note.getEtudiant().getId())
            );
        }

        return form;
    }

}
