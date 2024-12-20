package model;

import manager.InMemoryTasksManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicEqualsOnlyIfIdIsEqual() {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        int epicId1 = manager.generateId();
        Epic epic1 = new Epic("Epic name", "Epic description", epicId1);
        int epicId2 = manager.generateId();
        Epic epic2 = new Epic("Epic name", "Epic description", epicId2);

        assertNotEquals(epic1, epic2, "Epics must not be equal when IDs are different");

        int epicId3 = manager.generateId();
        Epic epic3 = new Epic("Epic name", "Epic description", epicId3);
        int epicId4 = epicId3;
        Epic epic4 = new Epic("Epic name", "Epic description", epicId4);
        assertEquals(epic3,epic4, "Epics must be equal when IDs are same");

    }

    @Test
    void epicCantAddItselfToHisOwnSubtaskList(){
        InMemoryTasksManager manager = new InMemoryTasksManager();
        int epicId = manager.generateId();
        Epic epic = new Epic("Epic name", "Epic description", epicId);

        assertEquals(-1, epic.addSubTaskIdToEpic(epic), "Epic cant add itself to his own subtaskList");

    }
}