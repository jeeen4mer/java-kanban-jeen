package handler;

import org.junit.jupiter.api.Test;
import model.Task;
import model.TaskStatus;
import server.TestBase;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest extends TestBase {
    @Test
    void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description");
        task.setTaskStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest("/tasks", "POST", taskJson);
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendRequest("/tasks", "GET", "");
        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Test"));
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description");
        taskManager.addTaskToList(task);

        HttpResponse<String> response = sendRequest("/tasks/" + task.getId(), "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description");
        taskManager.addTaskToList(task);
        task.setName("Updated");
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest("/tasks", "POST", taskJson);
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendRequest("/tasks/" + task.getId(), "GET", "");
        assertTrue(getResponse.body().contains("Updated"));
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description");
        taskManager.addTaskToList(task);

        HttpResponse<String> deleteResponse = sendRequest("/tasks/" + task.getId(), "DELETE", "");
        assertEquals(200, deleteResponse.statusCode());

        HttpResponse<String> getResponse = sendRequest("/tasks/" + task.getId(), "GET", "");
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Description1");
        Task task2 = new Task("Test2", "Description2");
        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);

        HttpResponse<String> response = sendRequest("/tasks", "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test1"));
        assertTrue(response.body().contains("Test2"));
    }

    @Test
    void testDeleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Description1");
        Task task2 = new Task("Test2", "Description2");
        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);

        HttpResponse<String> deleteResponse = sendRequest("/tasks", "DELETE", "");
        assertEquals(200, deleteResponse.statusCode());

        HttpResponse<String> getResponse = sendRequest("/tasks", "GET", "");
        assertEquals("[]", getResponse.body().trim());
    }

    @Test
    void testTaskNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/tasks/999", "GET", "");
        assertEquals(404, response.statusCode());
    }

    @Test
    void testInvalidTaskId() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/tasks/invalid", "GET", "");
        assertEquals(400, response.statusCode());
    }

    @Test
    void testOverlappingTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Description1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task1);

        Task task2 = new Task("Test2", "Description2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(15));
        task2.setDuration(Duration.ofMinutes(30));
        String taskJson = gson.toJson(task2);

        HttpResponse<String> response = sendRequest("/tasks", "POST", taskJson);
        assertEquals(406, response.statusCode());
    }
}