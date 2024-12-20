package manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTasksManager;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void historyListIsNotLimitedByTen() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 0; i < 15; i++) {
            int taskId = manager.generateId();
            Task task = new Task(taskId, "Task name", "Task description");
            historyManager.add(task);
        }
        assertEquals(10, historyManager.getHistory().size(), "List must contain only 10 elements");
    }

    @Test
    void methodClearHistoryDeleteNodes() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        HistoryManager historyManager = new InMemoryHistoryManager();
        int taskId1 = manager.generateId();
        Task task1 = new Task(taskId1, "Task name", "Task description");
        historyManager.add(task1);
        int taskId2 = manager.generateId();
        Task task2 = new Task(taskId2, "Task name", "Task description");
        historyManager.add(task2);

        assertFalse(historyManager.getHistory().isEmpty(), "History must be not empty");
        historyManager.clearHistoryList();
        assertTrue(historyManager.getHistory().isEmpty(), "History must be empty");
    }

    @Test
    void addingTaskToHistoryManagerNotChangeTask() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        HistoryManager historyManager = new InMemoryHistoryManager();
        int taskId1 = manager.generateId();
        Task task1 = new Task(taskId1, "Task name", "Task description");
        historyManager.add(task1);

        Task taskFromHistoryList = historyManager.getHistory().get(0);
        assertEquals(taskFromHistoryList, task1, "Tasks must be equal");
    }

    @Test
    void checkAddOrder() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        HistoryManager historyManager = new InMemoryHistoryManager();
        int taskId1 = manager.generateId();
        Task task1 = new Task(taskId1, "Task name", "Task description");
        historyManager.add(task1);
        int taskId2 = manager.generateId();
        Task task2 = new Task(taskId2, "Task name2", "Task description2");
        historyManager.add(task2);

        int taskId3 = manager.generateId();
        Task task3 = new Task(taskId3, "Task name3", "Task description3");
        historyManager.add(task3);
        assertEquals(task3, historyManager.getHistory().get(0), "Last element must be at first place");
        assertEquals(task2, historyManager.getHistory().get(1), "Before last element must be at second place");
    }

    @Test
    void addSameTaskToHistoryListWillReplaceItInLastPlace() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        HistoryManager historyManager = new InMemoryHistoryManager();
        int taskId1 = manager.generateId();
        Task task1 = new Task(taskId1, "Task name", "Task description");
        historyManager.add(task1);
        int taskId2 = manager.generateId();
        Task task2 = new Task(taskId2, "Task name2", "Task description2");
        historyManager.add(task2);
        int taskId3 = manager.generateId();
        Task task3 = new Task(taskId3, "Task name3", "Task description3");
        historyManager.add(task3);
        historyManager.add(task1);
        assertEquals(task1, historyManager.getHistory().get(0), "Task1 must be at first place");
        assertEquals(task3, historyManager.getHistory().get(1), "Task3 must be at second place");

    }
}