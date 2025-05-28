package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Task;
import model.SubTask;

import java.io.IOException;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {
    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 2) {
            List<Task> subtasks = taskManager.getAllTasksList().stream()
                    .filter(t -> t instanceof SubTask)
                    .toList();
            sendSuccess(exchange, gson.toJson(subtasks));
        } else if (parts.length == 3 && parts[2].matches("\\d+")) {
            int id = Integer.parseInt(parts[2]);
            Task task = taskManager.getTaskById(id);
            if (task instanceof SubTask subtask) {
                sendSuccess(exchange, gson.toJson(subtask));
            } else {
                sendNotFound(exchange, "Подзадача с id " + id + " не найдена");
            }
        } else {
            sendBadRequest(exchange, "Неверный формат запроса");
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        try {
            String json = readRequestBody(exchange);
            SubTask subtask = gson.fromJson(json, SubTask.class);

            if (subtask == null) {
                sendBadRequest(exchange, "Некорректные данные подзадачи");
                return;
            }

            int epicId = subtask.getRelationEpicId();
            if (epicId <= 0) {
                sendBadRequest(exchange, "Не указан ID эпика для подзадачи");
                return;
            }

            Task task = taskManager.getTaskById(epicId);
            if (!(task instanceof Epic)) {
                sendBadRequest(exchange, "Указанный эпик не существует");
                return;
            }

            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                if (taskManager.isOverlapping(subtask)) {
                    sendHasInteractions(exchange, "Подзадача пересекается по времени с существующими задачами");
                    return;
                }
            }

            if (subtask.getId() == 0) {
                taskManager.addSubTaskToList(subtask);
                sendSuccess(exchange, "{\"message\": \"Подзадача добавлена\"}");
            } else {
                taskManager.updateTask(subtask);
                sendSuccess(exchange, "{\"message\": \"Подзадача обновлена\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendBadRequest(exchange, "Ошибка при обработке данных подзадачи: " + e.getMessage());
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            path = path.replaceAll("^/+", "").replaceAll("/+$", "");

            if (path.equals("subtasks")) {  // Изменено условие проверки пути
                taskManager.deleteAllSubtasks();
                sendSuccess(exchange, "{\"message\": \"Все подзадачи удалены\"}");
                return;
            }

            if (path.startsWith("subtasks/") && path.substring(9).matches("\\d+")) {
                int id = Integer.parseInt(path.substring(9));
                Task task = taskManager.getTaskById(id);

                if (task == null) {
                    sendNotFound(exchange, "Подзадача с id " + id + " не найдена");
                    return;
                }

                if (!(task instanceof SubTask)) {
                    sendBadRequest(exchange, "Задача с id " + id + " не является подзадачей");
                    return;
                }

                taskManager.deleteById(id);
                sendSuccess(exchange, "{\"message\": \"Подзадача удалена\"}");
                return;
            }

            sendBadRequest(exchange, "Неверный путь для удаления");
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при обработке: " + e.getMessage());
        }
    }
}