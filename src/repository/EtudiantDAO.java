package repository;

import core.annotations.Inject;
import entity.Etudiant;

import javax.persistence.EntityManager;

public class EtudiantDAO extends BaseDAO<Etudiant> {

    public EtudiantDAO(@Inject EntityManager em) {
        super(em);
    }

}
