package entity;

import java.io.Serializable;

public interface BaseEntity extends Serializable {
    int getId();
    void setId(Integer id);
}
