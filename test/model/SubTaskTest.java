package test.model;

import manager.InMemoryTasksManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SubTaskTest {

    private InMemoryTasksManager manager;
    private Epic epic;
    private SubTask subTask1;


    @BeforeEach
    void setUp() {
        manager = new InMemoryTasksManager();
        epic = new Epic("Эпик", "Описание эпика");
        manager.addEpicToList(epic);
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1");
        manager.addSubTaskToList(subTask1);
        subTask1.setRelationEpicId(epic.getId());
    }
    @Test
    void testSubTaskCreationWithRelation() {
        assertEquals(subTask1.getRelationEpicId(), epic.getId());
    }

    @Test
    void testSubtaskCreationWithoutRelation() {
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2");
        manager.addSubTaskToList(subTask2);
        assertNotEquals(subTask2.getRelationEpicId(), epic.getId());
    }
    @Test
    void testSubTaskEquals() {
        Task subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3");
        Task subTask4 = new SubTask("Подзадача 3", "Описание подзадачи 3");
        manager.addSubTaskToList((SubTask) subTask3);
        manager.addSubTaskToList((SubTask) subTask4);

        assertEquals(subTask3, subTask4);
    }
    @Test
    void testSubTaskHashCode() {
        SubTask subTask5 = new SubTask("Подзадача 5", "Описание подзадачи 5");
        SubTask subTask6 = new SubTask("Подзадача 6", "Описание подзадачи 6");

        manager.addSubTaskToList(subTask5);
        manager.addSubTaskToList(subTask6);
        assertNotEquals(subTask5.hashCode(), subTask6.hashCode());
    }
    @Test
    void testSubTaskToString() {
        SubTask subTask7 = new SubTask("Подзадача 7", "Описание подзадачи 7");
        manager.addSubTaskToList(subTask7);
        String expectedToString = "{id=" + subTask7.getId() + ", name='Подзадача 7', description='Описание подзадачи 7', status=NEW, relatedEpic id=" + epic.getId() +"}";
        assertEquals(subTask7.toString(), expectedToString);
    }

}