package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 2) {
            List<Task> epics = taskManager.getAllTasksList().stream()
                    .filter(t -> t instanceof Epic)
                    .toList();
            sendSuccess(exchange, gson.toJson(epics));
            return;
        }

        if (parts.length >= 3 && parts[2].matches("\\d+")) {
            int id = Integer.parseInt(parts[2]);
            Task task = taskManager.getTaskById(id);

            if (!(task instanceof Epic)) {
                sendNotFound(exchange, "Эпик с id " + id + " не найден");
                return;
            }

            if (parts.length == 4 && "subtasks".equals(parts[3])) {
                List<SubTask> subtasks = taskManager.getAllSubtaskOfEpic(id);
                sendSuccess(exchange, gson.toJson(subtasks));
                return;
            }

            sendSuccess(exchange, gson.toJson(task));
            return;
        }

        sendBadRequest(exchange, "Неверный формат запроса");
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        try {
            String json = readRequestBody(exchange);
            Epic epic = gson.fromJson(json, Epic.class);

            if (epic == null) {
                sendBadRequest(exchange, "Некорректные данные эпика");
                return;
            }

            if (epic.getId() == 0) {
                taskManager.addEpicToList(epic);
                sendSuccess(exchange, "{\"message\": \"Эпик добавлен\"}");
            } else {
                Task existingTask = taskManager.getTaskById(epic.getId());
                if (!(existingTask instanceof Epic)) {
                    sendBadRequest(exchange, "Задача с id " + epic.getId() + " не является эпиком");
                    return;
                }

                Epic existingEpic = (Epic) existingTask;
                epic.clearSubTaskIds();
                for (Integer subTaskId : existingEpic.getSubTaskIds()) {
                    epic.addSubTaskIdToEpic(subTaskId);
                }

                taskManager.updateTask(epic);
                sendSuccess(exchange, "{\"message\": \"Эпик обновлен\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendBadRequest(exchange, "Ошибка при обработке данных эпика: " + e.getMessage());
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            String cleanPath = path.replaceAll("^/+", "").replaceAll("/+$", "");

            if ("epics".equals(cleanPath)) {
                taskManager.deleteAllEpics();
                sendSuccess(exchange, "{\"message\": \"Все эпики удалены\"}");
                return;
            }

            if (cleanPath.startsWith("epics/") && cleanPath.substring(6).matches("\\d+")) {
                int id = Integer.parseInt(cleanPath.substring(6));
                Task task = taskManager.getTaskById(id);

                if (task == null) {
                    sendNotFound(exchange, "Эпик с id " + id + " не найден");
                    return;
                }

                if (!(task instanceof Epic)) {
                    sendBadRequest(exchange, "Задача с id " + id + " не является эпиком");
                    return;
                }

                taskManager.deleteById(id);
                sendSuccess(exchange, "{\"message\": \"Эпик удален\"}");
                return;
            }

            sendBadRequest(exchange, "Неверный путь для удаления");
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при обработке: " + e.getMessage());
        }
    }
}