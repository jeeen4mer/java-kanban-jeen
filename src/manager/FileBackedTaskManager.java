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
            writer.write("id,type,name,status,description,epic,startTime,duration\n");

            // Сохраняем все задачи, включая эпики и подзадачи
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
        Map<Integer, Task> tempTasks = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) {
                return manager;
            }

            // Сначала читаем все задачи и создаем их объекты
            boolean isHistory = false;
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
                    }
                }
            }

            // Теперь добавляем задачи в менеджер в правильном порядке
            // Сначала эпики
            for (Task task : tempTasks.values()) {
                if (task instanceof Epic) {
                    manager.epicsList.put(task.getId(), (Epic) task);
                }
            }

            // Затем обычные задачи
            for (Task task : tempTasks.values()) {
                if (!(task instanceof Epic) && !(task instanceof SubTask)) {
                    manager.tasksList.put(task.getId(), task);
                }
            }

            // Наконец, подзадачи и связываем их с эпиками
            for (Task task : tempTasks.values()) {
                if (task instanceof SubTask) {
                    SubTask subTask = (SubTask) task;
                    Epic epic = manager.epicsList.get(subTask.getRelationEpicId());
                    if (epic != null) {
                        manager.subtasksList.put(subTask.getId(), subTask);
                        epic.addSubTaskIdToEpic(subTask.getId());
                        epic.updateEpicTimeAndDuration(manager.getAllSubtaskOfEpic(epic.getId()));
                    }
                }
            }

            // Восстанавливаем историю
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (isHistory) {
                    try {
                        int id = Integer.parseInt(line);
                        Task historyTask = manager.getTaskById(id);
                        if (historyTask != null) {
                            manager.historyManager.add(historyTask);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (line.equals("# HISTORY")) {
                    isHistory = true;
                }
            }

            // Обновляем счетчик ID
            int maxId = tempTasks.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
            manager.idCounter = maxId;


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
        Task existingTask = getTaskById(updatedTask.getId());
        if (existingTask != null) {
            super.updateTask(updatedTask);
            save();
        }
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

    @Override
    public void checkAndSetEpicStatus(int epicId) {
        super.checkAndSetEpicStatus(epicId);
        save(); // Сохраняем изменения после обновления статуса
    }
}