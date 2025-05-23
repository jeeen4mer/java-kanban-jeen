package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }


    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskIdToEpic(int subtaskId) {
        subTaskIds.add(subtaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(id, epic.id) && Objects.equals(name, epic.name) && Objects.equals(description, epic.description) && taskStatus == epic.taskStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus);
    }

    @Override
    public String toString() {
        return "{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", status=" + taskStatus + "}";
    }
}