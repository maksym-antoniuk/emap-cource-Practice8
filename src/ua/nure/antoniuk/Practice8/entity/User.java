package ua.nure.antoniuk.Practice8.entity;

import java.io.Serializable;

/**
 * Created by Max on 21.12.2017.
 */
public class User implements Serializable {
    private int id;
    private String login;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }

    public static User createUser(String login){
        User user = new User();
        user.setLogin(login);
        return user;
    }
}
