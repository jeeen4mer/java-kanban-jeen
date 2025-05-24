package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIds = new ArrayList<>();

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration = Duration.ZERO;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskIdToEpic(int subtaskId) {
        subTaskIds.add(subtaskId);
    }

    public void updateEpicTimeAndDuration(List<SubTask> allSubtasks) {
        List<LocalDateTime> startTimes = new ArrayList<>();
        List<LocalDateTime> endTimes = new ArrayList<>();
        Duration totalDuration = Duration.ZERO;

        for (Integer subtaskId : subTaskIds) {
            SubTask subtask = allSubtasks.stream()
                    .filter(s -> s.getId() == subtaskId)
                    .findFirst()
                    .orElse(null);

            if (subtask != null && subtask.getStartTime() != null) {
                startTimes.add(subtask.getStartTime());
                endTimes.add(subtask.getEndTime());
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        this.startTime = startTimes.isEmpty() ? null : startTimes.stream().min(LocalDateTime::compareTo).orElse(null);
        this.endTime = endTimes.isEmpty() ? null : endTimes.stream().max(LocalDateTime::compareTo).orElse(null);
        this.duration = totalDuration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(startTime, epic.startTime) &&
                Objects.equals(endTime, epic.endTime) &&
                Objects.equals(duration, epic.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startTime, endTime, duration);
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