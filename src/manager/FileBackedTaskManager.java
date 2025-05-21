package manager;

import model.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTasksManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasksList()) {
                writer.write(taskToString(task) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    private String taskToString(Task task) {
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

    private Task taskFromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) return null;

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        return switch (type) {
            case "TASK" -> new Task(name, description);
            case "EPIC" -> new Epic(name, description);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new SubTask(name, description);
            }
            default -> null;
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());

            if (lines.size() <= 1) return manager;

            for (int i = 1; i < lines.size(); i++) {
                Task task = manager.taskFromString(lines.get(i));
                if (task == null) continue;

                if (task instanceof Task && !(task instanceof Epic || task instanceof SubTask)) {
                    manager.addTaskToList(task);
                }
                if (task instanceof Epic) {
                    manager.addEpicToList((Epic) task);
                }
                if (task instanceof SubTask) {
                    SubTask subtask = (SubTask) task;
                    manager.addSubTaskToList(subtask);
                    if (manager.epicsList.containsKey(subtask.getRelationEpicId())) {
                        manager.epicsList.get(subtask.getRelationEpicId()).addSubTaskIdToEpic(subtask.getId());
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + e.getMessage());
        }

        return manager;
    }

    @Override
    public void addTaskToList(Task newTask) {
        super.addTaskToList(newTask);
        save();
    }

    @Override
    public void addEpicToList(Epic newEpic) {
        super.addEpicToList(newEpic);
        save();
    }

    @Override
    public void addSubTaskToList(SubTask newSubtask) {
        super.addSubTaskToList(newSubtask);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void deleteById(int idToRemove) {
        super.deleteById(idToRemove);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}