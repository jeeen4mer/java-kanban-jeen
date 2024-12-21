package model;

import java.util.Objects;

public class SubTask extends Task {

    private int relationEpicId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public int getRelationEpicId() {
        return relationEpicId;
    }


    public void setRelationEpicId(int relationEpicId) {
        this.relationEpicId = relationEpicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(id, subTask.id) && Objects.equals(name, subTask.name) && Objects.equals(description, subTask.description) && taskStatus == subTask.taskStatus && relationEpicId == subTask.relationEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus, relationEpicId);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                ", relatedEpic id=" + relationEpicId +
                '}';
    }
}