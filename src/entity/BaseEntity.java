package entity;

import core.FrontController;
import core.utils.Fetchable;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable, Fetchable {

    public abstract Integer getId();
    public abstract void setId(Integer id);

    public boolean equals(Object other) {
        if (! (other instanceof BaseEntity)) {
            return false;
        }
        return this.getId().intValue() == ((BaseEntity) other).getId().intValue();
    }

    public int hashCode() {
        return this.getId().intValue();
    }

    BaseEntity(){ }

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
