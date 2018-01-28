package repository;

import core.annotations.Inject;
import core.utils.Encoder;
import entity.Admin;
import entity.Etudiant;
import entity.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class AdminDAO extends BaseDAO<Admin> {

    public AdminDAO(@Inject EntityManager em) {
        super(em);
    }

    public boolean exists(String login) {
        try {
            em.createQuery("SELECT a FROM Admin a WHERE a.login = :login", Admin.class)
                    .setParameter("login", login)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public User authenticate(String login, String password) {
        String encoded = Encoder.md5(password);
        try {
            return em.createQuery("SELECT a FROM Admin a WHERE a.login = :login AND a.password = :password", Admin.class)
                    .setParameter("login", login)
                    .setParameter("password", encoded)
                    .getSingleResult();
        } catch (NoResultException e) {
            try {
                return em.createQuery("SELECT e FROM Etudiant e WHERE e.email = :login AND e.password = :password", Etudiant.class)
                        .setParameter("login", login)
                        .setParameter("password", encoded)
                        .getSingleResult();
            } catch (NoResultException e2) {
                return null;
            }
        }
    }

}
