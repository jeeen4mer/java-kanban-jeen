package manager;

import manager.InMemoryTasksManager;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTasksManagerTest {

    @Test
    void  allManagersReturnNotNullManagers() {
        InMemoryTasksManager tasksManager = new InMemoryTasksManager();
        assertNotNull(tasksManager, "TasksManager must be not null");
    }

    @Test
    void  tasksManagerCanAddTaskToList() {
        InMemoryTasksManager tasksManager = new InMemoryTasksManager();
        int taskId = tasksManager.generateId();
        Task task = new Task(taskId,"Task name", "Task description");
        tasksManager.addTaskToList(task);
        int id = task.getId();
        assertNotNull(tasksManager.getTaskById(id), "Task must be not null");
        assertEquals(task, tasksManager.getTaskById(id), "Task must be equals");
    }
}