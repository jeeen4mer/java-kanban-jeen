package model;

import java.util.Objects;

//
public class Task {
    private String name;
    private String description;
    private Integer id;
    private static int count = 0;
    private TaskStatus taskStatus;

    private int relationEpicId;

    public Task(String name, String description) {
        count++;
        id = count;
        this.taskStatus = TaskStatus.NEW;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int id) {
        this(name, description);
        this.id = id;
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
