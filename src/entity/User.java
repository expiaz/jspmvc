package entity;

public interface User {

    void setLogin(String login);
    String getLogin();

    void setPassword(String password);
    String getPassword();

    Object getId();

    boolean isAdmin();

}
