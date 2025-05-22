package manager;

import model.*;

public final class FormatterUtil {
    private FormatterUtil() {}

    public static String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getClass().getSimpleName().toUpperCase()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getTaskStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        if (task instanceof SubTask) {
            sb.append(((SubTask) task).getRelationEpicId());
        }

        return sb.toString();
    }

    public static Task taskFromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) return null;

        try {
            int id = Integer.parseInt(parts[0]);
            String type = parts[1];
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];

            switch (type) {
                case "TASK":
                    Task task = new Task(name, description);
                    task.setId(id);
                    task.setTaskStatus(status);
                    return task;
                case "EPIC":
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setTaskStatus(status);
                    return epic;
                case "SUBTASK":
                    int epicId = Integer.parseInt(parts[5]);
                    SubTask subTask = new SubTask(name, description);
                    subTask.setId(id);
                    subTask.setTaskStatus(status);
                    subTask.setRelationEpicId(epicId);
                    return subTask;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат числа");
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: Недопустимый аргумент");
            return null;
        }
    }
}