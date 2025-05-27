package handler;

import org.junit.jupiter.api.Test;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
import server.TestBase;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest extends TestBase {

    @Test
    void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        HttpResponse<String> response = sendRequest("/epics", "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic"));
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        HttpResponse<String> response = sendRequest("/epics/" + epic.getId(), "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic"));
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subtask);

        HttpResponse<String> response = sendRequest("/epics/" + epic.getId() + "/subtasks", "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    void testUpdateEpicStatus() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setRelationEpicId(epic.getId());
        subtask.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubTaskToList(subtask);

        String epicJson = gson.toJson(epic);
        HttpResponse<String> response = sendRequest("/epics", "POST", epicJson);
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendRequest("/epics/" + epic.getId(), "GET", "");
        assertTrue(getResponse.body().contains("IN_PROGRESS"));
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("New Epic", "Description");
        String epicJson = gson.toJson(epic);

        HttpResponse<String> response = sendRequest("/epics", "POST", epicJson);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("добавлен"));
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        HttpResponse<String> response = sendRequest("/epics/" + epic.getId(), "DELETE", "");
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendRequest("/epics/" + epic.getId(), "GET", "");
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        HttpResponse<String> response = sendRequest("/epics", "DELETE", "");
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendRequest("/epics", "GET", "");
        assertEquals("[]", getResponse.body().trim());
    }

    @Test
    void testEpicNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/epics/999", "GET", "");
        assertEquals(404, response.statusCode());
    }

    @Test
    void testBadRequest() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/epics/invalid", "GET", "");
        assertEquals(400, response.statusCode());
    }
}