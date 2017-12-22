package repository;

import core.annotations.Inject;
import entity.BaseEntity;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseDAO<T extends BaseEntity> {

    private EntityManager em;

    protected Class<T> entity;
    protected String table;

    public BaseDAO(@Inject EntityManager em) {
        this.em = em;
        this.entity = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.table = this.entity.getSimpleName();
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public T getById(int id) {
        return em.find(entity, id);
    }

    public void insert(T upplet) {
        em.getTransaction().begin();

        em.persist(upplet);

        em.getTransaction().commit();
    }

    public void update(T upplet) {
        em.getTransaction().begin();

        em.merge(upplet);

        em.getTransaction().commit();
    }

    public void remove(T upplet) {
        em.getTransaction().begin();

        em.createQuery("DELETE FROM " + this.table + " t WHERE t.id = :id")
                .setParameter("id", upplet.getId())
                .executeUpdate();

        em.getTransaction().commit();
    }

    public List<T> getAll() {
        return em.createQuery("SELECT t FROM " + this.table + " t").getResultList();
    }

}
