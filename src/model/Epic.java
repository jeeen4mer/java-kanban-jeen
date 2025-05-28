package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;
    private transient LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskIds = new ArrayList<>();
        this.setTaskStatus(TaskStatus.NEW);
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void addSubTaskIdToEpic(int subTaskId) {
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove(Integer.valueOf(subTaskId));
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateEpicTimeAndDuration(List<SubTask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStartTime(null);
            setDuration(null);
            this.endTime = null;
            return;
        }

        LocalDateTime minStartTime = subtasks.stream()
                .map(Task::getStartTime)
                .filter(time -> time != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxEndTime = subtasks.stream()
                .map(Task::getEndTime)
                .filter(time -> time != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subtasks.stream()
                .map(Task::getDuration)
                .filter(duration -> duration != null)
                .reduce(Duration.ZERO, Duration::plus);

        setStartTime(minStartTime);
        setDuration(totalDuration);
        this.endTime = maxEndTime;

        updateEpicStatus(subtasks);
    }

    private void updateEpicStatus(List<SubTask> subtasks) {
        if (subtasks.isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtasks.stream().allMatch(task -> task.getTaskStatus() == TaskStatus.NEW);
        boolean allDone = subtasks.stream().allMatch(task -> task.getTaskStatus() == TaskStatus.DONE);
        boolean hasNewOrDone = subtasks.stream()
                .anyMatch(task -> task.getTaskStatus() == TaskStatus.NEW
                        || task.getTaskStatus() == TaskStatus.DONE);

        if (allNew) {
            setTaskStatus(TaskStatus.NEW);
        } else if (allDone) {
            setTaskStatus(TaskStatus.DONE);
        } else if (hasNewOrDone) {
            setTaskStatus(TaskStatus.IN_PROGRESS);
        } else {
            setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return String.format("{id=%d, name='%s', description='%s', status=%s}",
                getId(), getName(), getDescription(), getTaskStatus());
    }
}