package repository;

import core.annotations.Inject;
import entity.Groupe;

import javax.persistence.EntityManager;

public class GroupeDAO extends BaseDAO<Groupe> {

    public GroupeDAO(@Inject EntityManager em) {
        super(em);
    }

}
