package model;

import java.util.Objects;

public class Node {
    protected Integer id;
    protected String name;
    protected String description;

    public Node(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }
}