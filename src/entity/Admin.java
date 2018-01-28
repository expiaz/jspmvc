package entity;

import com.sun.istack.internal.NotNull;
import core.utils.Encoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Admin extends BaseEntity implements User {

    public Admin(){}

    public Admin(String login, String pwd) {
        this.setLogin(login);
        this.setPassword(pwd);
    }

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    @NotNull
    private String password;

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String getLogin() {
        return this.login;
    }

    @Override
    public void setPassword(String password) {
        this.password = Encoder.md5(password);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
