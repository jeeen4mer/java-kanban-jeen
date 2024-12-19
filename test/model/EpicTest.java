package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic1 = new Epic("Epic name", "Epic description");
    Epic epic2 = new Epic("Epic name", "Epic description");

    @Test
    void epicEqualsOnlyIfIdIsEqual() {
        assertNotEquals(epic1, epic2);
    }

    @Test
    void epicCantAddItselfToHisOwnSubtaskList() {
        //попытка добавить другой Эпик как подзадачу
        assertEquals(1, epic1.addSubTaskIdToEpic(epic2));
        //попытка добавить сам Эпик как подзадачу для самого себя
        assertEquals(-1, epic1.addSubTaskIdToEpic(epic1));
    }
}