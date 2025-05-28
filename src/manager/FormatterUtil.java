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
        String type = task instanceof SubTask ? "SUBTASK" :
                task instanceof Epic ? "EPIC" : "TASK";
        String epicId = task instanceof SubTask ? String.valueOf(((SubTask) task).getRelationEpicId()) : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getTaskStatus(),
                task.getDescription(),
                epicId,
                startTime,
                duration);
    }


    public static Task taskFromString(String value) {
        String[] parts = value.split(",", -1); // Используем -1, чтобы включать пустые значения в конце
        if (parts.length < 8) return null; // Теперь проверяем на 8 частей

        try {
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

            if (!parts[6].isEmpty()) {
                task.setStartTime(LocalDateTime.parse(parts[6]));
            }
            if (!parts[7].isEmpty()) {
                task.setDuration(Duration.ofMinutes(Long.parseLong(parts[7])));
            }

            return task;
        } catch (Exception e) {
            return null;
        }
    }
}