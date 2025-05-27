package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTasksManager implements TaskManager {
    protected final Map<Integer, Task> tasksList;
    protected final Map<Integer, Epic> epicsList;
    protected final Map<Integer, SubTask> subtasksList;
    protected final TreeSet<Task> prioritizedTasks;
    protected final HistoryManager historyManager;
    protected int idCounter;

    public InMemoryTasksManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subtasksList = new HashMap<>();
        prioritizedTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime() == null && t2.getStartTime() == null) return t1.getId() - t2.getId();
            if (t1.getStartTime() == null) return 1;
            if (t2.getStartTime() == null) return -1;
            int compare = t1.getStartTime().compareTo(t2.getStartTime());
            return compare != 0 ? compare : t1.getId() - t2.getId();
        });
        historyManager = Managers.getDefaultHistory();
        idCounter = 0;
    }

    @Override
    public void addTaskToList(Task newTask) {
        if (newTask == null) return;
        newTask.setId(generateId());
        tasksList.put(newTask.getId(), newTask);
        updatePrioritizedTasks(newTask);
    }

    @Override
    public void addEpicToList(Epic newEpic) {
        if (newEpic == null) return;
        newEpic.setId(generateId());
        epicsList.put(newEpic.getId(), newEpic);
        updatePrioritizedTasks(newEpic);
    }

    @Override
    public void addSubTaskToList(SubTask newSubtask) {
        if (newSubtask == null) return;
        if (!epicsList.containsKey(newSubtask.getRelationEpicId())) {
            return;
        }
        newSubtask.setId(generateId());
        subtasksList.put(newSubtask.getId(), newSubtask);

        Epic epic = epicsList.get(newSubtask.getRelationEpicId());
        epic.addSubTaskIdToEpic(newSubtask.getId());
        epic.updateEpicTimeAndDuration(getAllSubtaskOfEpic(epic.getId()));

        updatePrioritizedTasks(newSubtask);
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (updatedTask == null) return;

        if (updatedTask instanceof Epic epic) {
            if (epicsList.containsKey(epic.getId())) {
                epicsList.put(epic.getId(), epic);
                epic.updateEpicTimeAndDuration(getAllSubtaskOfEpic(epic.getId()));
                updatePrioritizedTasks(epic);
            }
        } else if (updatedTask instanceof SubTask subTask) {
            if (subtasksList.containsKey(subTask.getId())) {
                subtasksList.put(subTask.getId(), subTask);
                Epic epic = epicsList.get(subTask.getRelationEpicId());
                if (epic != null) {
                    epic.updateEpicTimeAndDuration(getAllSubtaskOfEpic(epic.getId()));
                }
                updatePrioritizedTasks(subTask);
            }
        } else if (tasksList.containsKey(updatedTask.getId())) {
            tasksList.put(updatedTask.getId(), updatedTask);
            updatePrioritizedTasks(updatedTask);
        }
    }

    private void updatePrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void deleteById(int idToRemove) {
        Task task = getTaskById(idToRemove);
        if (task == null) return;

        if (task instanceof SubTask subTask) {
            Epic epic = epicsList.get(subTask.getRelationEpicId());
            if (epic != null) {
                epic.getSubTaskIds().remove(Integer.valueOf(idToRemove)); // Изменение здесь
                epic.updateEpicTimeAndDuration(getAllSubtaskOfEpic(epic.getId()));
            }
            subtasksList.remove(idToRemove);
        } else if (task instanceof Epic epic) {
            epic.getSubTaskIds().forEach(subtaskId -> {
                subtasksList.remove(subtaskId);
                prioritizedTasks.removeIf(t -> t.getId() == subtaskId);
                historyManager.remove(subtaskId);
            });
            epicsList.remove(idToRemove);
        } else {
            tasksList.remove(idToRemove);
        }

        prioritizedTasks.removeIf(t -> t.getId() == idToRemove);
        historyManager.remove(idToRemove);
    }


    @Override
    public void deleteAllTasks() {
        // Сначала получаем все ID задач
        List<Integer> taskIds = new ArrayList<>(tasksList.keySet());
        taskIds.addAll(epicsList.keySet());
        taskIds.addAll(subtasksList.keySet());

        // Удаляем задачи из истории
        for (Integer id : taskIds) {
            historyManager.remove(id);
        }

        // Очищаем все коллекции
        tasksList.clear();
        epicsList.clear();
        subtasksList.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epicsList.values()) {
            prioritizedTasks.remove(epic);
            historyManager.remove(epic.getId());
            epic.getSubTaskIds().forEach(subtaskId -> {
                prioritizedTasks.removeIf(t -> t.getId() == subtaskId);
                historyManager.remove(subtaskId);
            });
        }
        epicsList.clear();
        subtasksList.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (SubTask subTask : subtasksList.values()) {
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
        }
        subtasksList.clear();
        for (Epic epic : epicsList.values()) {
            epic.getSubTaskIds().clear();
            epic.updateEpicTimeAndDuration(Collections.emptyList());
        }
    }

    @Override
    public void removeSubtasksOfEpic(int epicId) {
        Epic epic = epicsList.get(epicId);
        if (epic == null) return;

        epic.getSubTaskIds().forEach(subtaskId -> {
            subtasksList.remove(subtaskId);
            prioritizedTasks.removeIf(t -> t.getId() == subtaskId);
            historyManager.remove(subtaskId);
        });
        epic.getSubTaskIds().clear();
        epic.updateEpicTimeAndDuration(Collections.emptyList());
    }

    @Override
    public List<SubTask> getAllSubtaskOfEpic(int epicId) {
        return subtasksList.values().stream()
                .filter(s -> s.getRelationEpicId() == epicId)
                .toList();
    }

    @Override
    public Task getTaskById(int idToFind) {
        Task foundTask = tasksList.get(idToFind);
        if (foundTask == null) {
            foundTask = epicsList.get(idToFind);
        }
        if (foundTask == null) {
            foundTask = subtasksList.get(idToFind);
        }
        if (foundTask != null) {
            historyManager.add(foundTask);
        }
        return foundTask;
    }

    @Override
    public List<Task> getAllTasksList() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasksList.values());
        allTasks.addAll(epicsList.values());
        allTasks.addAll(subtasksList.values());
        return allTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        // Создаем новый список из всех задач (включая задачи, подзадачи, но не эпики)
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasksList.values());
        allTasks.addAll(subtasksList.values());

        // Сортируем по startTime, помещая задачи без времени в конец
        return allTasks.stream()
                .sorted((t1, t2) -> {
                    if (t1.getStartTime() == null && t2.getStartTime() == null) {
                        return 0;
                    }
                    if (t1.getStartTime() == null) {
                        return 1;
                    }
                    if (t2.getStartTime() == null) {
                        return -1;
                    }
                    return t1.getStartTime().compareTo(t2.getStartTime());
                })
                .collect(Collectors.toList());
    }


    @Override
    public void removeTaskFromHistoryList(int id) {
        historyManager.remove(id);
    }


    @Override
    public boolean isOverlapping(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null) {
            return false;
        }

        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        return prioritizedTasks.stream()
                .filter(t -> t.getId() != newTask.getId() && t.getStartTime() != null)
                .anyMatch(t -> {
                    LocalDateTime existingStart = t.getStartTime();
                    LocalDateTime existingEnd = t.getEndTime();
                    return !(existingEnd.isBefore(newStart) || newEnd.isBefore(existingStart));
                });
    }

    protected int generateId() {
        return ++idCounter;
    }
}