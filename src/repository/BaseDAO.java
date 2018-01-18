package repository;

import core.annotations.Inject;
import core.utils.Fetchable;
import core.utils.Fetcher;
import entity.BaseEntity;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseDAO<T extends BaseEntity> implements Fetcher<T> {

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

        em.createQuery("DELETE FROM " + this.table + " t WHERE t.id = :id", this.entity)
                .setParameter("id", upplet.getId())
                .executeUpdate();

        em.getTransaction().commit();
    }

    public List<T> getAll() {
        return em.createQuery("SELECT t FROM " + this.table + " t", this.entity).getResultList();
    }

    @Override
    public T fetch(Object value) {
        try {
            return this.getById(Integer.valueOf((String) value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
