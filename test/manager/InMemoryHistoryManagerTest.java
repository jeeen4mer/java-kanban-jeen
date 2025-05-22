package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    private TaskManager taskManager;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);
    }
    @Test
    void testAdd() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не может быть null.");
        assertEquals(1, history.size(), "Неверный размер истории.");
        assertEquals(task1, history.get(0), "Неверная задача в истории.");
    }

    @Test
    void testAddAndRemoveFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не может быть null.");
        assertEquals(1, history.size(), "Неверный размер истории.");
        assertEquals(task2, history.get(0), "Неверная задача в истории.");
    }
    @Test
    void testAddAndClearFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.clearHistoryList();
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
    @Test
    void testEmptyHistory() {
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

}