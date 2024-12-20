package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private TaskStatus taskStatus;
    private int relationEpicId;

    //конструктор для новых объектов, id получаем в InMemoryTasksManager
    public Task(String name, String description) {
        this.taskStatus = TaskStatus.NEW;
        this.name = name;
        this.description = description;
    }

    //конструктор для загрузки данных из файла. id задается из файла
    public Task(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    //конструктор для id
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "{id=" + getId() + ", name='" + getName() + "', description='" + getDescription() + "', status="
                + getTaskStatus() + "'}\n";
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

    public int getRelationEpicId() {
        return relationEpicId;
    }

    public int setRelationEpicId(Task epicTask) {
        if (epicTask.getId().equals(this.getId())) {
            return -1;
        }
        this.relationEpicId = epicTask.getId();
        return 1;
    }
}