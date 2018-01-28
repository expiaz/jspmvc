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
import middleware.EtudiantMiddleware;
import repository.EtudiantDAO;
import repository.ModuleDAO;
import repository.NoteDAO;
import service.NoteService;

import java.security.InvalidParameterException;

@PathPrefix("/etudiant")
@Viewspace(namespace = "etudiant", path = "@view/etudiant/")
public class EtudiantController extends BaseController {

    private EtudiantDAO dao;

    public EtudiantController(@Inject Container container, @Inject EtudiantDAO dao) {
        super(container);
        this.dao = dao;
    }

    @Route(name = "etudiant.list", path = "/all", before = AdminMiddleware.class)
    public Response listAction() {
        return this.render("@etudiant/list",
            new ParameterBag()
                .add("etudiants", dao.getAll())
        );
    }

    @Route(name = "etudiant.add", path = "/add", methods = {HttpMethod.POST, HttpMethod.GET}, before = AdminMiddleware.class)
    public Response addAction(Request request) {

        Response form = this.render("@etudiant/add");

        if(request.isPost()) {
            String nom = request.getParameter("nom", "");
            String prenom = request.getParameter("prenom", "");
            String mail = request.getParameter("mail", "");
            String pwd = request.getParameter("password", "");
            String pwd2 = request.getParameter("password2", "");

            if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty()
                || pwd.isEmpty() || pwd2.isEmpty() || !pwd.equals(pwd2) || pwd.length() < 8
                || mail.indexOf("@") <= 0) {
                this.addError("Champs invalides");
                return form;
            }

            Etudiant etudiant = new Etudiant(nom, prenom);
            etudiant.setEmail(mail);
            etudiant.setPassword(pwd);
            this.dao.insert(etudiant);

            return this.redirectToRoute("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId()));
        }

        return form;
    }

    @Route(name = "etudiant.show", path = "/{etudiant}", before = EtudiantMiddleware.class)
    public Response showAction(@Parameter(name = "etudiant", mask = "\\d+") Etudiant etudiant) {
        return this.render("@etudiant/show",
                new ParameterBag()
                    .add("etudiant", etudiant)
        );
    }

    @Route(name = "etudiant.edit", path = "/edit/{etudiant}", methods = {HttpMethod.POST, HttpMethod.GET}, before = AdminMiddleware.class)
    public Response editAction(Request request,
                               @Parameter(name = "etudiant", mask = "\\d+") Etudiant etudiant) {

        Response form = this.render("@etudiant/edit",
                new ParameterBag()
                        .add("etudiant", etudiant)
        );

        if(request.isPost()) {

            String nom = request.getParameter("nom", "");
            String prenom = request.getParameter("prenom", "");

            if (nom.isEmpty() || prenom.isEmpty()) {
                this.addError("Nom ou prénom invalide");
                return form;
            }

            etudiant.setNom(nom);
            etudiant.setPrenom(prenom);
            this.dao.update(etudiant);

            return this.redirectToRoute("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId()));
        }

        return form;
    }

    @Route(name = "etudiant.module.add", path = "/{etudiant}/add/module", methods = {HttpMethod.GET, HttpMethod.POST}, before = AdminMiddleware.class)
    public Response addModuleAction(Request request,
                                    @Parameter(name = "etudiant", mask = "\\d+") Etudiant etudiant)
    {
        ModuleDAO moduleDAO = (ModuleDAO) this.container.get(ModuleDAO.class);

        Response form = this.render("@etudiant/module/add",
                new ParameterBag()
                        .add("etudiant", etudiant)
                        .add("modules", moduleDAO.getAll())
        );

        if(request.isPost()) {
            Module module;
            String moduleId = request.getParameter("module");
            try {
                module = moduleDAO.getById(Integer.valueOf(moduleId));
                if (module == null) {
                    throw new InvalidParameterException();
                }
            } catch (Exception e) {
                this.addError(moduleId + " n'est pas un module adéquat");
                return form;
            }

            etudiant.addModule(module);
            module.addEtudiant(etudiant);
            this.dao.update(etudiant);
            moduleDAO.update(module);

            return this.redirectToRoute("etudiant.show",
                new ParameterBag().add("etudiant", etudiant.getId())
            );
        }

        return form;
    }

    @Route(name = "etudiant.note.add", path = "/{etudiant}/add/note", methods = {HttpMethod.GET, HttpMethod.POST}, before = AdminMiddleware.class)
    public Response addNoteAction(Request request,
                                  @Parameter(name = "etudiant", mask = "\\d+") Etudiant etudiant)
    {
        Response form = this.render("@etudiant/note/add",
                new ParameterBag()
                        .add("etudiant", etudiant)
        );

        NoteService noteService = (NoteService) this.container.get(NoteService.class);
        NoteDAO noteDAO = (NoteDAO) this.container.get(NoteDAO.class);
        ModuleDAO moduleDAO = (ModuleDAO) this.container.get(ModuleDAO.class);

        if(request.isPost()) {
            Module module;
            try {
                module = moduleDAO.getById(Integer.valueOf(request.getParameter("module")));
                if (module == null) {
                    throw new InvalidParameterException();
                }
            } catch (Exception e) {
                this.addError("Le module selectionné est erroné");
                return this.render("@etudiant/note/add",
                        new ParameterBag()
                            .add("etudiant", etudiant)
                );
            }

            Note note;
            try {
                note = new Note();
                note.setValeur(noteService.isNoteValid(request.getParameter("note"), true) /*Float.valueOf(request.getParameter("note"))*/);
            } catch (Exception e) {
                this.addError("La note entrée est erronée");
                return form;
            }

            note.setEtudiant(etudiant);
            note.setModule(module);

            noteDAO.insert(note);

            module.addNote(note);
            etudiant.addNote(note);

            moduleDAO.update(module);
            this.dao.update(etudiant);

            return this.redirectToRoute("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId()));
        }

        return form;
    }

}
