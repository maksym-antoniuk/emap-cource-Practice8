package ua.nure.antoniuk.Practice8.entity;

import javafx.scene.layout.GridPane;

import java.io.Serializable;

/**
 * Created by Max on 21.12.2017.
 */
public class Group implements Serializable {

    private int id;
    private String name;

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);
        return group;
    }
}
