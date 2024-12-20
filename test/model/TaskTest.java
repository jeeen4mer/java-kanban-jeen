package model;

import manager.InMemoryTasksManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTaskEqualsOnlyIfIdIsEqual() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        int taskId1 = manager.generateId();
        Task task1 = new Task("Task name", "Task description", taskId1);
        int taskId2 = manager.generateId();
        Task task2 = new Task("Task name", "Task description", taskId2);

        assertNotEquals(task1, task2, "Tasks must not be equal when IDs are different");


        int taskId3 = manager.generateId();
        Task task3 = new Task("Task name", "Task description", taskId3);
        int taskId4 = taskId3;
        Task task4 = new Task("Task name", "Task description", taskId4);


        assertEquals(task3,task4, "Tasks must be equal when IDs are same");

    }
}