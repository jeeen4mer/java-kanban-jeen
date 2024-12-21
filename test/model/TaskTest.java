package test.model;

import manager.InMemoryTasksManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {
    private InMemoryTasksManager manager;
    private Task task1;


    @BeforeEach
    void setUp() {
        manager = new InMemoryTasksManager();
        task1 = new Task("Задача 1", "Описание задачи 1");
        manager.addTaskToList(task1);
    }

    @Test
    void testTaskStatusChange() {
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task1.getTaskStatus());

        manager.updateTask(task1);
        assertEquals(TaskStatus.DONE, task1.getTaskStatus());

    }
    @Test
    void testTaskEquals() {
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 2", "Описание задачи 2");
        manager.addTaskToList(task2);
        manager.addTaskToList(task3);
        assertEquals(task2, task3);
    }
    @Test
    void testTaskHashCode() {
        Task task4 = new Task("Задача 4", "Описание задачи 4");
        Task task5 = new Task("Задача 5", "Описание задачи 5");
        manager.addTaskToList(task4);
        manager.addTaskToList(task5);
        assertNotEquals(task4.hashCode(), task5.hashCode());
    }
    @Test
    void testTaskToString() {
        String expectedToString = "{id=" + task1.getId() + ", name='Задача 1', description='Описание задачи 1', status=NEW}";
        assertEquals(task1.toString(), expectedToString);
    }


}