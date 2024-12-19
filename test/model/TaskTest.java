package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task1 = new Task("Task name", "Task description");
    Task task2 = new Task("Task name", "Task description");

    @Test
    void testTaskEqualsOnlyIfIdIsEqual() {
        assertNotEquals(task1, task2);
    }
}