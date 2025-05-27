package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 2) {
            List<Task> tasks = taskManager.getAllTasksList().stream()
                    .filter(t -> !(t instanceof Epic || t instanceof SubTask))
                    .toList();
            sendSuccess(exchange, gson.toJson(tasks));
        } else if (parts.length == 3 && parts[2].matches("\\d+")) {
            int id = Integer.parseInt(parts[2]);
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                sendSuccess(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange, "Задача с id " + id + " не найдена");
            }
        } else {
            sendBadRequest(exchange, "Неверный формат запроса");
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        try {
            String json = readRequestBody(exchange);
            Task task = gson.fromJson(json, Task.class);

            if (task == null) {
                sendBadRequest(exchange, "Некорректные данные задачи");
                return;
            }

            // Проверка на пересечение только если есть время старта и продолжительность
            if (task.getStartTime() != null && task.getDuration() != null) {
                if (taskManager.isOverlapping(task)) {
                    sendHasInteractions(exchange, "Задача пересекается по времени с существующими задачами");
                    return;
                }
            }

            // Проверяем id. Если null или 0, то это новая задача
            if (task.getId() == 0) {
                taskManager.addTaskToList(task);
                sendSuccess(exchange, "{\"message\": \"Задача добавлена\"}");
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange, "{\"message\": \"Задача обновлена\"}");
            }
        } catch (Exception e) {
            e.printStackTrace(); // для отладки
            sendBadRequest(exchange, "Ошибка при обработке данных задачи: " + e.getMessage());
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 2) {
            taskManager.deleteAllTasks();
            sendSuccess(exchange, "{\"message\": \"Все задачи удалены\"}");
        } else if (parts.length == 3 && parts[2].matches("\\d+")) {
            int id = Integer.parseInt(parts[2]);
            taskManager.deleteById(id);
            sendSuccess(exchange, "{\"message\": \"Задача удалена\"}");
        } else {
            sendBadRequest(exchange, "Неверный формат запроса для удаления");
        }
    }
}