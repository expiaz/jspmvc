package entity;

import core.annotations.Fetchable;

import java.io.Serializable;
import java.lang.annotation.Annotation;

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
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Fetchable.class;
    }
}
