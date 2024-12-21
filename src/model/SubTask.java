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
        return Objects.equals(id, subTask.id);
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
                ", status=" + taskStatus + '\'' +
                ", relatedEpic id=" + relationEpicId +
                '}';
    }
}