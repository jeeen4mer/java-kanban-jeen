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

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasksList()) {
                writer.write(FormatterUtil.taskToString(task) + "\n");
            }

            writer.write("\n# HISTORY\n");
            for (Task historyTask : getHistory()) {
                writer.write(historyTask.getId() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        boolean isHistorySection = false;

        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.equals("# HISTORY")) {
                    isHistorySection = true;
                    continue;
                }

                if (!isHistorySection) {
                    Task task = FormatterUtil.taskFromString(line);
                    if (task == null) continue;

                    if (task instanceof Task && !(task instanceof Epic || task instanceof SubTask)) {
                        manager.addTaskToList(task);
                    } else if (task instanceof Epic) {
                        manager.addEpicToList((Epic) task);
                    } else if (task instanceof SubTask) {
                        SubTask subtask = (SubTask) task;
                        manager.addSubTaskToList(subtask);
                        if (manager.epicsList.containsKey(subtask.getRelationEpicId())) {
                            manager.epicsList.get(subtask.getRelationEpicId()).addSubTaskIdToEpic(subtask.getId());
                        }
                    }
                } else {
                    try {
                        int id = Integer.parseInt(line);
                        Task task = manager.getTaskById(id);
                        if (task != null) {
                            manager.historyManager.add(task);
                        }
                    } catch (NumberFormatException ignored) {
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