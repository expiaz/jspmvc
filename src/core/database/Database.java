package core.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Database {

    private static final String DBMS = "SQLITE";
    private static Database instance = null;

    private EntityManagerFactory emFactory;

    public static Database getInstance() {
        if(! (instance instanceof Database)) {
            instance = new Database();
        }
        return instance;
    }

    private Database() {
        this.emFactory = Persistence.createEntityManagerFactory(DBMS);
        EntityManager em = this.emFactory.createEntityManager();
    }

    public EntityManager getEntityManager() {
        return this.emFactory.createEntityManager();
    }

    public void destroy() {
        this.emFactory.close();
    }

}
