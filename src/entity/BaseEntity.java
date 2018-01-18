package entity;

import core.FrontController;
import core.utils.Fetchable;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable, Fetchable {

    public abstract int getId();
    public abstract void setId(Integer id);

    BaseEntity(){

    }

    @Override
    public Class from() {
        try {
            return Class.forName("repository." + this.getClass().getSimpleName() + "DAO");
        } catch (ClassNotFoundException e) {
            FrontController.die(BaseEntity.class, e);
            return null;
        }
    }
}
