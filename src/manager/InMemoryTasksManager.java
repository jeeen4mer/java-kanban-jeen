package manager;

import model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTasksManager implements TaskManager {
    protected Map<Integer, Task> tasksList = new HashMap<>();
    protected Map<Integer, Epic> epicsList = new HashMap<>();
    protected Map<Integer, SubTask> subtasksList = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>();
    protected int idCounter = 0;
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getAllTasksList() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasksList.values());
        allTasks.addAll(epicsList.values());
        allTasks.addAll(subtasksList.values());
        return allTasks;
    }

    @Override
    public void deleteAllTasks() {
        tasksList.clear();
        epicsList.clear();
        subtasksList.clear();
        prioritizedTasks.clear();
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
        if (isOverlapping(newTask)) {
            throw new IllegalStateException("Задача пересекается по времени с уже существующими задачами");
        }
        newTask.setId(generateId());
        tasksList.put(newTask.getId(), newTask);
        prioritizedTasks.add(newTask);
    }

    @Override
    public void addEpicToList(Epic newEpic) {
        newEpic.setId(generateId());
        epicsList.put(newEpic.getId(), newEpic);
        prioritizedTasks.add(newEpic);
    }

    @Override
    public void addSubTaskToList(SubTask newSubtask) {
        if (isOverlapping(newSubtask)) {
            throw new IllegalStateException("Подзадача пересекается по времени с уже существующими задачами");
        }
        newSubtask.setId(generateId());
        subtasksList.put(newSubtask.getId(), newSubtask);
        prioritizedTasks.add(newSubtask);
        if (epicsList.containsKey(newSubtask.getRelationEpicId())) {
            Epic relatedEpic = epicsList.get(newSubtask.getRelationEpicId());
            relatedEpic.addSubTaskIdToEpic(newSubtask.getId());
            relatedEpic.updateEpicTimeAndDuration(getAllSubtaskOfEpic(newSubtask.getRelationEpicId()));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            if (subtasksList.containsKey(task.getId())) {
                if (isOverlapping(subTask)) {
                    throw new IllegalStateException("Обновленная подзадача пересекается по времени с существующими задачами");
                }
                prioritizedTasks.remove(subtasksList.get(task.getId()));
                subtasksList.put(task.getId(), subTask);
                prioritizedTasks.add(subTask);
                checkAndSetEpicStatus(subTask.getRelationEpicId());
            }
        } else if (task instanceof Epic) {
            if (epicsList.containsKey(task.getId())) {
                prioritizedTasks.remove(epicsList.get(task.getId()));
                epicsList.put(task.getId(), (Epic) task);
                prioritizedTasks.add(task);
            }
        } else {
            if (tasksList.containsKey(task.getId())) {
                if (isOverlapping(task)) {
                    throw new IllegalStateException("Обновленная задача пересекается по времени с существующими задачами");
                }
                prioritizedTasks.remove(tasksList.get(task.getId()));
                tasksList.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void deleteById(int idToRemove) {
        if (tasksList.containsKey(idToRemove)) {
            prioritizedTasks.remove(tasksList.get(idToRemove));
            tasksList.remove(idToRemove);
        } else if (epicsList.containsKey(idToRemove)) {
            Epic epic = epicsList.get(idToRemove);
            prioritizedTasks.remove(epic);
            epicsList.remove(idToRemove);
            removeSubtasksOfEpic(epic.getId());
        } else if (subtasksList.containsKey(idToRemove)) {
            SubTask subtask = subtasksList.get(idToRemove);
            prioritizedTasks.remove(subtask);
            subtasksList.remove(idToRemove);
            for (Epic epic : epicsList.values()) {
                epic.getSubTaskIds().removeIf(id -> id == subtask.getId());
            }
        }
        historyManager.remove(idToRemove);
    }

    @Override
    public void removeSubtasksOfEpic(int id) {
        List<SubTask> subtasksToRemove = getAllSubtaskOfEpic(id);
        subtasksToRemove.forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            subtasksList.remove(subtask.getId());
        });
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
        return new ArrayList<>(prioritizedTasks);
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

    @Override
    public void checkAndSetEpicStatus(int epicId) {
        Epic epic = epicsList.get(epicId);
        if (epic == null) return;

        List<SubTask> subtasks = getAllSubtaskOfEpic(epicId);
        if (subtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = subtasks.stream().allMatch(task -> task.getTaskStatus() == TaskStatus.DONE);
        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
            return;
        }

        boolean allNew = subtasks.stream().allMatch(task -> task.getTaskStatus() == TaskStatus.NEW);
        if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        epic.setTaskStatus(TaskStatus.IN_PROGRESS);
    }
}