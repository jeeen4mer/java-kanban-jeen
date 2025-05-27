package manager;

import model.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTasksManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
                save();
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось создать файл: " + e.getMessage());
            }
        }
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");

            for (Task task : getAllTasksList()) {
                writer.write(FormatterUtil.taskToString(task) + "\n");
            }

            writer.write("\n# HISTORY\n");
            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                for (Task task : history) {
                    writer.write(String.valueOf(task.getId()) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return manager;
        }

        Map<Integer, Task> tempTasks = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) {
                return manager;
            }

            int maxId = 0;
            boolean isHistory = false;
            List<Integer> historyIds = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equals("# HISTORY")) {
                    isHistory = true;
                    continue;
                }

                if (!isHistory) {
                    Task task = FormatterUtil.taskFromString(line);
                    if (task != null) {
                        tempTasks.put(task.getId(), task);
                        maxId = Math.max(maxId, task.getId());
                    }
                } else {
                    try {
                        historyIds.add(Integer.parseInt(line));
                    } catch (NumberFormatException ignored) {}
                }
            }

            // Восстанавливаем задачи в правильном порядке
            for (Task task : tempTasks.values()) {
                if (task instanceof Epic) {
                    manager.epicsList.put(task.getId(), (Epic) task);
                } else if (!(task instanceof SubTask)) {
                    manager.tasksList.put(task.getId(), task);
                }
            }

            // Восстанавливаем подзадачи и связи с эпиками
            for (Task task : tempTasks.values()) {
                if (task instanceof SubTask subTask) {
                    Epic epic = manager.epicsList.get(subTask.getRelationEpicId());
                    if (epic != null) {
                        manager.subtasksList.put(subTask.getId(), subTask);
                        epic.addSubTaskIdToEpic(subTask.getId());
                    }
                }
            }

            // Обновляем времена эпиков
            for (Epic epic : manager.epicsList.values()) {
                epic.updateEpicTimeAndDuration(manager.getAllSubtaskOfEpic(epic.getId()));
            }

            // Восстанавливаем историю
            for (Integer id : historyIds) {
                Task task = manager.getTaskById(id);
                if (task != null) {
                    manager.historyManager.add(task);
                }
            }

            manager.idCounter = maxId;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + e.getMessage());
        }

        return manager;
    }

    @Override
    public void addTaskToList(Task task) {
        super.addTaskToList(task);
        save();
    }

    @Override
    public void addEpicToList(Epic epic) {
        super.addEpicToList(epic);
        save();
    }

    @Override
    public void addSubTaskToList(SubTask subtask) {
        super.addSubTaskToList(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void deleteById(int id) {
        super.deleteById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}