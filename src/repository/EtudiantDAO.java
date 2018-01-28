package repository;

import core.annotations.Inject;
import entity.Etudiant;
import entity.Module;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

public class EtudiantDAO extends BaseDAO<Etudiant> {

    public EtudiantDAO(@Inject EntityManager em) {
        super(em);
    }

    public List<Module> getNonAffectedModules(Etudiant etudiant) {
        return this.getEntityManager().createQuery("SELECT m FROM Module m WHERE :etudiant NOT IN (m.etudiants)")
            .setParameter("etudiant", etudiant)
            .getResultList();
    }

}
