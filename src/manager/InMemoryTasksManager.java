package manager;

import model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTasksManager implements TaskManager {
    protected Map<Integer, Task> tasksList = new HashMap<>();
    protected Map<Integer, Epic> epicsList = new HashMap<>();
    protected Map<Integer, SubTask> subtasksList = new HashMap<>();
    protected int idCounter = 0;
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getAllTasksList() {
        List<Task> allTasks = new ArrayList<>();

        // Добавляем обычные задачи
        allTasks.addAll(tasksList.values());

        // Добавляем эпики
        allTasks.addAll(epicsList.values());

        // Добавляем подзадачи
        allTasks.addAll(subtasksList.values());

        return allTasks;
    }

    @Override
    public void deleteAllTasks() {
        tasksList.clear();
        epicsList.clear();
        subtasksList.clear();
        historyManager.clearHistoryList();
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
    public void addTaskToList(Task newTask) {
        newTask.setId(generateId());
        tasksList.put(newTask.getId(), newTask);
    }

    @Override
    public void addEpicToList(Epic newEpic) {
        newEpic.setId(generateId());
        epicsList.put(newEpic.getId(), newEpic);
    }

    @Override
    public void addSubTaskToList(SubTask newSubtask) {
        newSubtask.setId(generateId());
        subtasksList.put(newSubtask.getId(), newSubtask);
        if (epicsList.containsKey(newSubtask.getRelationEpicId())) {
            Epic relatedEpic = epicsList.get(newSubtask.getRelationEpicId());
            relatedEpic.addSubTaskIdToEpic(newSubtask.getId());
            ((Epic) relatedEpic).updateEpicTimeAndDuration(getAllSubtaskOfEpic(newSubtask.getRelationEpicId()));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            if (subtasksList.containsKey(task.getId())) {
                subtasksList.put(task.getId(), subTask);
                checkAndSetEpicStatus(subTask.getRelationEpicId());
            }
        } else if (task instanceof Epic) {
            if (epicsList.containsKey(task.getId())) {
                epicsList.put(task.getId(), (Epic) task);
            }
        } else {
            if (tasksList.containsKey(task.getId())) {
                tasksList.put(task.getId(), task);
            }
        }
    }


    public void checkAndSetEpicStatus(int epicId) {
        Epic epic = epicsList.get(epicId);
        if (epic == null) return;

        List<SubTask> subTasks = getAllSubtaskOfEpic(epicId);

        if (subTasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (SubTask subTask : subTasks) {
            TaskStatus status = subTask.getTaskStatus();
            if (status != TaskStatus.DONE) {
                allDone = false;
            }
            if (status != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void deleteById(int idToRemove) {
        if (tasksList.containsKey(idToRemove)) {
            tasksList.remove(idToRemove);
        } else if (epicsList.containsKey(idToRemove)) {
            Epic epic = epicsList.remove(idToRemove);
            removeSubtasksOfEpic(epic.getId());
        } else if (subtasksList.containsKey(idToRemove)) {
            SubTask subtask = subtasksList.remove(idToRemove);
            for (Epic epic : epicsList.values()) {
                epic.getSubTaskIds().removeIf(id -> id == subtask.getId());
            }
        }
        historyManager.remove(idToRemove);
    }

    @Override
    public void removeSubtasksOfEpic(int id) {
        List<Integer> toRemove = subtasksList.values().stream()
                .filter(s -> s.getRelationEpicId() == id)
                .map(Task::getId)
                .toList();

        toRemove.forEach(subtasksList::remove);
    }

    @Override
    public List<SubTask> getAllSubtaskOfEpic(int id) {
        return subtasksList.values().stream()
                .filter(s -> s.getRelationEpicId() == id)
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeTaskFromHistoryList(int id) {
        historyManager.remove(id);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> allTasks = new ArrayList<>();

        // Сначала добавляем задачи с установленным временем
        List<Task> tasksWithTime = Stream.concat(
                        tasksList.values().stream(),
                        Stream.concat(
                                epicsList.values().stream(),
                                subtasksList.values().stream()
                        )
                ).filter(t -> t.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());

        allTasks.addAll(tasksWithTime);

        // Добавляем задачи без времени
        Stream.concat(
                        tasksList.values().stream(),
                        Stream.concat(
                                epicsList.values().stream(),
                                subtasksList.values().stream()
                        )
                ).filter(t -> t.getStartTime() == null)
                .forEach(allTasks::add);

        return allTasks;
    }

    @Override
    public boolean isOverlapping(Task newTask) {
        if (newTask.getStartTime() == null) return false;

        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newStart.plus(newTask.getDuration());

        return Stream.concat(tasksList.values().stream(), subtasksList.values().stream())
                .filter(t -> t.getStartTime() != null && !t.equals(newTask))
                .anyMatch(t -> {
                    LocalDateTime existingStart = t.getStartTime();
                    LocalDateTime existingEnd = existingStart.plus(t.getDuration());
                    return !existingEnd.isBefore(newStart) && !newEnd.isBefore(existingStart);
                });
    }

    private int generateId() {
        return ++idCounter;
    }
}