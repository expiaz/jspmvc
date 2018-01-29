package controller;

import core.annotations.*;
import core.http.HttpMethod;
import core.http.Request;
import core.http.Response;
import core.utils.Container;
import core.utils.ParameterBag;
import entity.Etudiant;
import entity.Module;
import entity.Note;
import middleware.AdminMiddleware;
import repository.EtudiantDAO;
import repository.ModuleDAO;
import repository.NoteDAO;
import service.NoteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PathPrefix("/module")
@Viewspace(namespace = "module", path = "@view/module/")
public class ModuleController extends BaseController{

    private ModuleDAO dao;

    public ModuleController(@Inject Container container, @Inject ModuleDAO dao) {
        super(container);
        this.dao = dao;
    }

    @Route(name = "module.list", path = "/all", before = AdminMiddleware.class)
    public Response listAction() {
        return this.render("@module/list",
                new ParameterBag()
                    .add("modules", dao.getAll())
        );
    }

    @Route(name = "module.show", path = "/{module}")
    public Response showAction(@Parameter(name = "module", mask = "\\d+") Module module) {
        return this.render("@module/show",
                new ParameterBag()
                    .add("module", module)
        );
    }

    @Route(name = "module.add", path = "/add", methods = {HttpMethod.POST, HttpMethod.GET}, before = AdminMiddleware.class)
    public Response addAction(Request request) {

        final Response form = this.render("@module/add");

        if(request.isPost()) {
            String nom = request.getParameter("nom", "");

            if (nom.isEmpty()) {
                this.addError("Nom invalide");
                return form;
            }

            Module module = new Module(nom);
            this.dao.insert(module);

            return this.redirectToRoute("module.show", new ParameterBag().add("module", module.getId()));
        }

        return form;
    }

    @Route(name = "module.edit", path = "/edit/{module}", methods = {HttpMethod.POST, HttpMethod.GET}, before = AdminMiddleware.class)
    public Response editAction(Request request, @Parameter(name = "module", mask = "\\d+") Module module)
    {

        Response form = this.render("@module/edit",
                new ParameterBag().add("module", module)
        );

        if(request.isPost()) {
            String nom = request.getParameter("nom", "");

            if (nom.isEmpty()) {
                this.addError("Nom invalide");
                return form;
            }

            module.setNom(nom);
            this.dao.update(module);

            return this.redirectToRoute("module.show", new ParameterBag().add("module", module.getId()));
        }

        return form;
    }

    @Route(name = "module.note.add", path = "/note/{module}/add", methods = {HttpMethod.POST, HttpMethod.GET}, before = AdminMiddleware.class)
    public Response addNoteAction(Request request, @Parameter(name = "module", mask = "\\d+") Module module)
    {
        Response form = this.render("@module/note/add",
            new ParameterBag().add("module", module)
        );

        NoteDAO noteDAO = (NoteDAO) this.container.get(NoteDAO.class);
        EtudiantDAO etudiantDAO = (EtudiantDAO) this.container.get(EtudiantDAO.class);
        Etudiant etudiant;
        Note vo;
        NoteService noteService = (NoteService) this.container.get(NoteService.class);
        List<Note> notes = new ArrayList();

        if(request.isPost()) {
            try {
                for(Map.Entry<String, String> note : request.getArrayParameter("notes").entrySet()) {
                    vo = new Note();
                    vo.setModule(module);
                    vo.setValeur(noteService.isNoteValid(note.getValue(), true));

                    etudiant = etudiantDAO.getById(Integer.valueOf(note.getKey()));
                    vo.setEtudiant(etudiant);

                    notes.add(vo);
                }

                for (Note note : notes) {
                    noteDAO.insert(note);
                    note.getEtudiant().addNote(note);
                    note.getModule().addNote(note);
                    etudiantDAO.update(note.getEtudiant());
                }

                this.dao.update(module);
            } catch (Exception e) {
                this.addError("Une des notes affectée est erronée");
                return form;
            }

            return this.redirectToRoute("module.show", new ParameterBag().add("module", module.getId()));
        }

        return form;
    }

}
