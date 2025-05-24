package manager;

import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public final class FormatterUtil {
    private FormatterUtil() {
    }

    public static String taskToString(Task task) {
        StringBuilder result = new StringBuilder();
        result.append(task.getId()).append(",");

        if (task instanceof SubTask) {
            result.append("SUBTASK,");
        } else if (task instanceof Epic) {
            result.append("EPIC,");
        } else {
            result.append("TASK,");
        }

        result.append(task.getName()).append(",")
                .append(task.getTaskStatus()).append(",")
                .append(task.getDescription()).append(",");

        if (task instanceof SubTask) {
            result.append(((SubTask) task).getRelationEpicId());
        }

        result.append(",");
        // Добавляем время и продолжительность
        result.append(task.getStartTime() != null ? task.getStartTime() : "")
                .append(",")
                .append(task.getDuration() != null ? task.getDuration().toMinutes() : "");

        return result.toString();
    }


    public static Task taskFromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 7) return null; // Минимальное количество частей

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        if (type.equals("SUBTASK")) {
            SubTask subTask = new SubTask(name, description);
            if (!parts[5].isEmpty()) {
                subTask.setRelationEpicId(Integer.parseInt(parts[5]));
            }
            task = subTask;
        } else if (type.equals("EPIC")) {
            task = new Epic(name, description);
        } else {
            task = new Task(name, description);
        }

        task.setId(id);
        task.setTaskStatus(status);

        // Устанавливаем время и продолжительность
        if (parts.length > 6 && !parts[6].isEmpty()) {
            task.setStartTime(LocalDateTime.parse(parts[6]));
        }
        if (parts.length > 7 && !parts[7].isEmpty()) {
            task.setDuration(Duration.ofMinutes(Long.parseLong(parts[7])));
        }

        return task;
    }

}