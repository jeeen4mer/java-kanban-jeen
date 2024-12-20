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
            ArrayList<SubTask> subtaskListForCopy = getAllSubtaskOfEpic(epicId);
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
        tasksList.put(newTask.getId(), newTask);
    }

    @Override
    public void addEpicToList(Epic newEpic) {
        epicsList.put(newEpic.getId(), newEpic);
    }

    @Override
    public void addSubTaskToList(SubTask newSubtask) {
        subtasksList.put(newSubtask.getId(), newSubtask);
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

        if (updatedTask.getClass().getName().equals("model.SubTask")) {
            SubTask updatedTaskCopy = (SubTask) updatedTask;
            Epic relatedEpic = epicsList.get(updatedTaskCopy.getRelationEpicId());
            checkAndSetEpicStatus(relatedEpic.getId());
        }
    }

    @Override
    public void checkAndSetEpicStatus(int epicId) {
        boolean isAllSubtasksDone = true;

        for (SubTask subtask : subtasksList.values()) {
            if (subtask.getRelationEpicId() == epicId) {
                if (subtask.getTaskStatus().equals(TaskStatus.NEW) ||
                        subtask.getTaskStatus().equals(TaskStatus.IN_PROGRESS)) {
                    isAllSubtasksDone = false;
                    break;
                }
            }
        }
        if (isAllSubtasksDone) {
            epicsList.get(epicId).setTaskStatus(TaskStatus.DONE);
        } else {
            epicsList.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void deleteById(int idToRemove) {
        if (tasksList.containsKey(idToRemove)) {
            tasksList.remove(idToRemove);
        }

        if (subtasksList.containsKey(idToRemove)) {
            subtasksList.remove(idToRemove);
        }
        if (epicsList.containsKey(idToRemove)) {
            epicsList.remove(idToRemove);
            removeSubtasksOfEpic(idToRemove);
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
             removeTaskFromHistoryList(idToRemove);
        }
    }

    @Override
    public ArrayList<SubTask> getAllSubtaskOfEpic(int id) {
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
    public int generateId(){
        return ++idCounter;
    }
}