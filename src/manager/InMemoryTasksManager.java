package manager;

import model.*;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class InMemoryTasksManager implements TaskManager {
    private Map<Integer, Task> tasksList = new HashMap<>();
    private Map<Integer, Epic> epicsList = new HashMap<>();
    private Map<Integer, SubTask> subtasksList = new HashMap<>();

    private int idCounter = 0; // Добавили счетчик ID


    private HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public List<Task> getAllTasksList() {
        List<Task> allTasksList = new ArrayList<>();
        allTasksList.addAll(tasksList.values());

        for (Epic epic : epicsList.values()) {
            allTasksList.add(epic);
            int epicId = epic.getId();
            List<SubTask> subtaskListForCopy = getAllSubtaskOfEpic(epicId);
            allTasksList.addAll(subtaskListForCopy);
        }

        return allTasksList;
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
        Task foundTask;
        if (tasksList.containsKey(idToFind)) {
            foundTask = tasksList.get(idToFind);
            historyManager.add(foundTask);
            return foundTask;
        }

        if (epicsList.containsKey(idToFind)) {
            foundTask = epicsList.get(idToFind);
            historyManager.add(foundTask);
            return foundTask;
        }

        foundTask = subtasksList.get(idToFind);
        historyManager.add(foundTask);
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
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        switch (updatedTask.getTaskStatus()) {
            case NEW:
                updatedTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                break;
            case IN_PROGRESS:
                updatedTask.setTaskStatus(TaskStatus.DONE);
        }


        if (updatedTask instanceof SubTask) {
            SubTask subtask = (SubTask) updatedTask;
            if (epicsList.containsKey(subtask.getRelationEpicId())) {
                checkAndSetEpicStatus(subtask.getRelationEpicId());
            }
        }
    }


    @Override
    public void checkAndSetEpicStatus(int epicId) {
        Epic epic = epicsList.get(epicId);
        if (epic == null) {
            return;
        }
        boolean allSubtasksDone = true;
        boolean hasInProgressSubtasks = false;
        for (SubTask subtask : subtasksList.values()) {
            if (subtask.getRelationEpicId() == epicId) {
                if (subtask.getTaskStatus() == TaskStatus.NEW || subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                    allSubtasksDone = false;
                }
                if  (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                    hasInProgressSubtasks = true;
                }
            }
        }
        if (allSubtasksDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (hasInProgressSubtasks) {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
        else {
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }


    @Override
    public void deleteById(int idToRemove) {
        if (tasksList.containsKey(idToRemove)) {
            tasksList.remove(idToRemove);
        }

        if (epicsList.containsKey(idToRemove)) {
            epicsList.remove(idToRemove);
            removeSubtasksOfEpic(idToRemove);
        }
        if (subtasksList.containsKey(idToRemove)) {
            subtasksList.remove(idToRemove);
        }

        removeTaskFromHistoryList(idToRemove);
    }

    @Override
    public void removeSubtasksOfEpic(int id) {
        ArrayList<Integer> idSubtasksToRemove = new ArrayList<>();

        for (SubTask subtaskToCheck : subtasksList.values()) {
            if (subtaskToCheck.getRelationEpicId() == id) {
                idSubtasksToRemove.add(subtaskToCheck.getId());
            }
        }

        for (Integer idToRemove : idSubtasksToRemove) {
            subtasksList.remove(idToRemove);
        }
    }

    @Override
    public List<SubTask> getAllSubtaskOfEpic(int id) {
        ArrayList<SubTask> epicRelatedSubtasks = new ArrayList<>();
        for (SubTask subtask : subtasksList.values()) {
            if (subtask.getRelationEpicId() == id) {
                epicRelatedSubtasks.add(subtask);
            }
        }

        return epicRelatedSubtasks;
    }

    @Override
    public List<Task> getHistory() {

        return historyManager.getHistory();
    }

    @Override
    public void removeTaskFromHistoryList(int id) {
        historyManager.remove(id);
    }

    // Метод для получения нового id
    private int generateId() {
        return ++idCounter;
    }
}