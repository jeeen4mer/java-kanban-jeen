package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    private Task task1;

    private Task task2;

    private Epic epic1;
    private SubTask subtask1;
    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        subtask1 = new SubTask("Подзадача 1", "Описание подзадачи 1");

        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);
        taskManager.addEpicToList(epic1);
        taskManager.addSubTaskToList(subtask1);
        subtask1.setRelationEpicId(epic1.getId());
    }

    @Test
    void testAddTask() {
        final List<Task> allTasks = taskManager.getAllTasksList();
        assertNotNull(allTasks);
        assertEquals(4, allTasks.size(), "Неверное количество задач.");
        assertTrue(allTasks.contains(task1), "Неверная задача в списке.");
        assertTrue(allTasks.contains(task2), "Неверная задача в списке.");
        assertTrue(allTasks.contains(epic1), "Неверный эпик в списке.");
        assertTrue(allTasks.contains(subtask1), "Неверная подзадача в списке.");


    }

    @Test
    void testDeleteAllTasks() {
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasksList().isEmpty(), "Список задач не пуст.");
        assertTrue(taskManager.getHistory().isEmpty(), "История не пуста");
    }

    @Test
    void testGetTaskById() {
        final Task foundTask1 = taskManager.getTaskById(task1.getId());
        final Task foundTask2 = taskManager.getTaskById(task2.getId());
        final Task foundEpic1 = taskManager.getTaskById(epic1.getId());
        final Task foundSubtask1 = taskManager.getTaskById(subtask1.getId());
        assertEquals(task1, foundTask1, "Неверная задача получена по ID");
        assertEquals(task2, foundTask2, "Неверная задача получена по ID");
        assertEquals(epic1, foundEpic1, "Неверный эпик получена по ID");
        assertEquals(subtask1, foundSubtask1, "Неверная подзадача получена по ID");
        assertNotNull(taskManager.getHistory());
        assertEquals(4, taskManager.getHistory().size(), "Неверное кол-во задач в истории");
    }

    @Test
    void testUpdateTask() {
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        assertEquals(TaskStatus.DONE, task1.getTaskStatus(), "Неверный статус после обновления");

    }
    @Test
    void testCheckAndSetEpicStatus() {
        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask1);
        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask1);
        assertEquals(TaskStatus.DONE, epic1.getTaskStatus());
    }


    @Test
    void testRemoveSubtasksOfEpic() {
        taskManager.deleteById(epic1.getId());
        assertTrue(taskManager.getAllSubtaskOfEpic(epic1.getId()).isEmpty());
        assertNull(taskManager.getTaskById(subtask1.getId()));
    }

    @Test
    void testGetHistory() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(subtask1.getId());

        assertEquals(4,taskManager.getHistory().size());
    }

    @Test
    void testRemoveTaskFromHistoryList() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.removeTaskFromHistoryList(task1.getId());

        assertEquals(1,taskManager.getHistory().size());

    }
    @Test
    void testEpicEquals() {
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Epic epic3 = new Epic("Эпик 2", "Описание эпика 2");
        assertEquals(epic2, epic3);
    }
    @Test
    void testSubTaskEquals() {
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3");
        SubTask subTask4 = new SubTask("Подзадача 3", "Описание подзадачи 3");
        assertEquals(subTask3, subTask4);
    }
    @Test
    void testSubTaskToString() {
        SubTask subTask7 = new SubTask("Подзадача 7", "Описание подзадачи 7");
        taskManager.addSubTaskToList(subTask7);
        subTask7.setRelationEpicId(epic1.getId());
        String expectedToString = "{id=" + subTask7.getId() + ", name='Подзадача 7', description='Описание подзадачи 7', status=NEW, relatedEpic id=" + epic1.getId() +"}";
        assertEquals(expectedToString, subTask7.toString());
    }
    @Test
    void testTaskEquals() {
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 2", "Описание задачи 2");
        assertEquals(task2, task3);
    }
}