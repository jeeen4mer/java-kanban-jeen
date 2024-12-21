package model;

import java.util.Objects;

public class Node {
    protected Integer id;
    protected String name;
    protected String description;

    public Node(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public Node( String name, String description) {

        this.name = name;
        this.description = description;
    }

    public Integer getId(){
        return id;
    }
}