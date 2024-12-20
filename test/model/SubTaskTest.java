package model;

import manager.InMemoryTasksManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testSubTaskEqualsOnlyIfIdIsEqual() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        int subtaskId1 = manager.generateId();
        SubTask subTask1 = new SubTask("SubTask name", "SubTask description", subtaskId1);
        int subtaskId2 = manager.generateId();
        SubTask subTask2 = new SubTask("SubTask name", "SubTask description", subtaskId2);
        assertNotEquals(subTask1, subTask2, "SubTasks must not be equal when IDs are different");

        int subtaskId3 = manager.generateId();
        SubTask subTask3 = new SubTask("SubTask name", "SubTask description", subtaskId3);
        int subtaskId4 = subtaskId3;
        SubTask subTask4 = new SubTask("SubTask name", "SubTask description", subtaskId4);
        assertEquals(subTask3, subTask4, "SubTasks must be equal when IDs are same");
    }

    @Test
    void subtaskCantAddItselfLikeHisOwnEpic(){
        InMemoryTasksManager manager = new InMemoryTasksManager();
        int epicId = manager.generateId();
        Epic epic = new Epic("Epic name", "Epic description", epicId);
        int subtaskId = manager.generateId();
        SubTask subTask = new SubTask("SubTask name", "SubTask description", subtaskId);
        assertEquals(-1, subTask.setRelationEpicId(subTask), "Subtask cant be like his own epic");

    }
}