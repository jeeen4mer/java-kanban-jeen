package handler;

import org.junit.jupiter.api.Test;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
import server.TestBase;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest extends TestBase {

    @Test
    void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setTaskStatus(TaskStatus.NEW);
        subtask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subtask);

        HttpResponse<String> response = sendRequest("/subtasks", "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setTaskStatus(TaskStatus.NEW);
        subtask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subtask);

        HttpResponse<String> response = sendRequest("/subtasks/" + subtask.getId(), "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setTaskStatus(TaskStatus.NEW);
        subtask.setRelationEpicId(epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> response = sendRequest("/subtasks", "POST", subtaskJson);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("добавлена"));
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setTaskStatus(TaskStatus.NEW);
        subtask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subtask);

        subtask.setTaskStatus(TaskStatus.IN_PROGRESS);
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> response = sendRequest("/subtasks", "POST", subtaskJson);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("обновлена"));
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setTaskStatus(TaskStatus.NEW);
        subtask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subtask);

        // Убедитесь, что путь сформирован верно
        String path = "/subtasks/" + subtask.getId();
        System.out.println("URL для удаления: " + path);

        HttpResponse<String> response = sendRequest(path, "DELETE", "");
        System.out.println("Ответ сервера: " + response.body());
        assertEquals(200, response.statusCode(), "Подзадача не была удалена");

        HttpResponse<String> getResponse = sendRequest(path, "GET", "");
        assertEquals(404, getResponse.statusCode(), "Ожидалось, что подзадача будет удалена");
    }

    @Test
    void testDeleteAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpicToList(epic);

        SubTask subtask = new SubTask("Subtask", "Description");
        subtask.setTaskStatus(TaskStatus.NEW);
        subtask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subtask);

        HttpResponse<String> response = sendRequest("/subtasks", "DELETE", "");
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendRequest("/subtasks", "GET", "");
        assertEquals("[]", getResponse.body().trim());
    }

    @Test
    void testSubtaskNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/subtasks/999", "GET", "");
        assertEquals(404, response.statusCode());
    }

    @Test
    void testInvalidSubtaskId() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/subtasks/invalid", "GET", "");
        assertEquals(400, response.statusCode());
    }
}