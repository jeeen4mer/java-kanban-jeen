package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    void addTaskToList(Task task);

    void addEpicToList(Epic epic);

    void addSubTaskToList(SubTask subtask);

    void updateTask(Task updatedTask);

    void deleteById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void removeSubtasksOfEpic(int epicId);

    Task getTaskById(int id);

    List<Task> getAllTasksList();

    List<SubTask> getAllSubtaskOfEpic(int epicId);

    List<Task> getHistory();

    void removeTaskFromHistoryList(int id);

    boolean isOverlapping(Task newTask);

    List<Task> getPrioritizedTasks();

}