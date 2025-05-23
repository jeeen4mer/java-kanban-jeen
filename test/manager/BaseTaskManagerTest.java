package manager;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseTaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    void setUp() {
        taskManager = createManager();
    }

    protected abstract T createManager();

    @Test
    void testAddTaskToList() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task);
        assertNotNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testGetTaskById() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);
        Task foundTask = taskManager.getTaskById(task.getId());
        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(TaskStatus.DONE, task.getTaskStatus());
    }

    @Test
    void testDeleteById() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);
        taskManager.deleteById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testPrioritizedTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStartTime(LocalDateTime.now().plusHours(2));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(45));

        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks);
        assertEquals(2, prioritizedTasks.size());
        assertEquals(task2.getStartTime(), prioritizedTasks.get(0).getStartTime());
        assertEquals(task1.getStartTime(), prioritizedTasks.get(1).getStartTime());
    }

    @Test
    void testIsOverlapping() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(15));
        task2.setDuration(Duration.ofMinutes(30));

        assertTrue(taskManager.isOverlapping(task2), "Задачи должны пересекаться");
    }
}