package repository;

import core.annotations.Inject;
import entity.Note;

import javax.persistence.EntityManager;

public class NoteDAO extends BaseDAO<Note> {

    public NoteDAO(@Inject EntityManager em) {
        super(em);
    }

}
