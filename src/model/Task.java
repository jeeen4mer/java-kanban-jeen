package model;

import java.util.Objects;

public class Task extends Node {

    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus = TaskStatus.NEW;


    public Task(int id, String name, String description) {
        super(id, name, description);
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description){
        super(name, description);
        this.name = name;
        this.description = description;
    }

    public void setId(int id){
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                '}';
    }
}