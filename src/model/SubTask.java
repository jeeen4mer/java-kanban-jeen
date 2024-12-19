package model;

public class SubTask extends Task {


    public SubTask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "{id=" + getId() + ", name='" + getName() + "', description='" + getDescription() + "', status="
                + getTaskStatus() + "', relatedEpic id=" + getRelationEpicId() + "}\n";
    }
}
