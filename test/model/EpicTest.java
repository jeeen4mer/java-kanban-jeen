package test.model;

import manager.InMemoryTasksManager;
import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private InMemoryTasksManager manager;
    private Epic epic1;
    private SubTask subTask;
    @BeforeEach
    void setUp() {
        manager = new InMemoryTasksManager();
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpicToList(epic1);
        subTask = new SubTask("Подзадача 1", "Описание подзадачи 1");
        manager.addSubTaskToList(subTask);
        subTask.setRelationEpicId(epic1.getId());
    }

    @Test
    void testAddSubTaskIdToEpic() {

        epic1.addSubTaskIdToEpic(subTask.getId());

        assertFalse(epic1.getSubTaskIds().isEmpty());
        assertEquals(epic1.getSubTaskIds().get(0), subTask.getId());
    }

    @Test
    void testEpicEquals() {
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Epic epic3 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addEpicToList(epic2);
        manager.addEpicToList(epic3);
        assertEquals(epic2, epic3);
    }
    @Test
    void testEpicHashCode() {
        Epic epic4 = new Epic("Эпик 4", "Описание эпика 4");
        Epic epic5 = new Epic("Эпик 5", "Описание эпика 5");
        manager.addEpicToList(epic4);
        manager.addEpicToList(epic5);
        assertNotEquals(epic4.hashCode(), epic5.hashCode());
    }
    @Test
    void testEpicToString() {
        String expectedToString = "{id=" + epic1.getId() + ", name='Эпик 1', description='Описание эпика 1', status=NEW}";
        assertEquals(epic1.toString(), expectedToString);
    }
}