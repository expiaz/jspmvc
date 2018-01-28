package repository;

import core.annotations.Inject;
import entity.Module;

import javax.persistence.EntityManager;

public class ModuleDAO extends BaseDAO<Module> {

    public ModuleDAO(@Inject EntityManager em) {
        super(em);
    }

}