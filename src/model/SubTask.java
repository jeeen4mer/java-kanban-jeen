package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private int relationEpicId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
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
        if (!(o instanceof SubTask subTask)) return false;
        if (!super.equals(o)) return false;
        return relationEpicId == subTask.relationEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relationEpicId);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{")
                .append("id=").append(id)
                .append(", name='").append(name).append("'")
                .append(", description='").append(description).append("'")
                .append(", status=").append(taskStatus)
                .append(", relatedEpic id=").append(relationEpicId);

        if (startTime != null && duration != null) {
            result.append(", startTime=").append(startTime)
                    .append(", duration=").append(duration.toMinutes()).append(" мин.");
        }

        result.append("}");
        return result.toString();
    }
}