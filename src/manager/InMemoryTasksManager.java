package manager;

import model.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;


public class InMemoryTasksManager implements TaskManager {
    private Map<Integer, Task> tasksList = new HashMap<>();
    private Map<Integer, Epic> epicsList = new HashMap<>();
    private Map<Integer, SubTask> subtasksList = new HashMap<>();

    //Список занятых id, чтобы нельзя было назначить уже используемый id,
    //использую String чтобы можно было удалять элемент по значению
    private Collection<String> idInUse = new ArrayList<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public List<Task> getAllTasksList() {
        List<Task> allTasksList = new ArrayList<>();
        allTasksList.addAll(tasksList.values());

        //немного усложнил, хотел сделать чтобы в списке всех задач был порядок,
        // сначала добавляется эпик, затем его подзадачи, затем следующий эпик и т.д.
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
            return  foundTask;
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
        idInUse.add(String.valueOf(newTask.getId()));
    }

    @Override
    public void addEpicToList(Epic newEpic) {
        epicsList.put(newEpic.getId(), newEpic);
        idInUse.add(String.valueOf(newEpic.getId()));
    }

    @Override
    public void addSubTaskToList(SubTask newSubtask) {
        subtasksList.put(newSubtask.getId(), newSubtask);
        idInUse.add(String.valueOf(newSubtask.getId()));
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

        if (updatedTask.getClass().getName().equals("Model.SubTask")) {
            SubTask updatedTaskCopy = (SubTask) updatedTask;
            Epic relatedEpic = epicsList.get(updatedTaskCopy.getRelationEpicId());
            checkAndSetEpicStatus(relatedEpic.getId());//добавил метод, чтобы разгрузить действующий метод
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
        //if all SubTasks is DONE, Model.Epic is DONE, else Model.Epic IN_PROGRESS because this method use only when
        // Model.SubTask status is updated
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
            idInUse.remove(String.valueOf(idToRemove));
        }

        if (subtasksList.containsKey(idToRemove)) {
            subtasksList.remove(idToRemove);
            idInUse.remove(String.valueOf(idToRemove));
        }
        //если удаляется эпик, удаляются все его подзадачи
        if (epicsList.containsKey(idToRemove)) {
            epicsList.remove(idToRemove);
            idInUse.remove(String.valueOf(idToRemove));
            removeSubtasksOfEpic(idToRemove);//добавил метод, чтобы разгрузить действующий метод
        }

        removeTaskFromHistoryList(idToRemove);
    }

    @Override
    public void removeSubtasksOfEpic(int id) {
        //создаю список, куда положу id подзадач для удаления, т.к. в foreach нельзя редактировать список, в цикле for
        // возникала ошибка, этот способ показался оптимальным из всех что я придумал)
        ArrayList<Integer> idSubtasksToRemove = new ArrayList<>();

        for (SubTask subtaskToCheck : subtasksList.values()) {
            if (subtaskToCheck.getRelationEpicId() == id) {
                idSubtasksToRemove.add(subtaskToCheck.getId());
            }
        }

        for (Integer idToRemove : idSubtasksToRemove) {
            subtasksList.remove(idToRemove);
            idInUse.remove(String.valueOf(idToRemove));
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
}
