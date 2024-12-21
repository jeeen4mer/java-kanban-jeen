package manager;

import model.Task;
import java.util.List;

public interface TaskManager {

    List<Task> getAllTasksList();

    void deleteAllTasks();

    Task getTaskById(int idToFind);

    void addTaskToList(Task newTask);

    void addEpicToList(model.Epic newEpic);

    void addSubTaskToList(model.SubTask newSubtask);

    void updateTask(Task updatedTask);

    void checkAndSetEpicStatus(int epicId);

    void deleteById(int idToRemove);

    void removeSubtasksOfEpic(int id);

    List<model.SubTask> getAllSubtaskOfEpic(int id);

    List<Task> getHistory();

    void removeTaskFromHistoryList(int id);
}