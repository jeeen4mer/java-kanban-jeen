import manager.InMemoryTaskManager;
import manager.Managers;
import enums.Status;
import task.Task;
import task.Epic;
import task.Subtask;

public class Main {

    private static final InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {

        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {
        Task buyProducts = new Task("Купить продукты", "Вместе с печеньем");
        inMemoryTaskManager.addTask(buyProducts);

        Task buyProductsToUpdate = new Task(buyProducts.getId(), "Не забыть купить продукты",
                "Можно без печенья", Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(buyProductsToUpdate);
        inMemoryTaskManager.addTask(new Task("Помыть окна", "Роботом для мойки окон"));


        Epic cleanHouse = new Epic("Прибраться в доме", "Выбросить ненужные вещи");
        inMemoryTaskManager.addEpic(cleanHouse);
        Subtask cleanHouseSubtask1 = new Subtask("Протереть пыль", "В том числе за диваном",
                cleanHouse.getId());
        Subtask cleanHouseSubtask2 = new Subtask("Пропылесосить", "Новым пылесосом",
                cleanHouse.getId());
        Subtask cleanHouseSubtask3 = new Subtask("Убрать мусор", "Сразу выбросить",
                cleanHouse.getId());
        inMemoryTaskManager.addSubtask(cleanHouseSubtask1);
        inMemoryTaskManager.addSubtask(cleanHouseSubtask2);
        inMemoryTaskManager.addSubtask(cleanHouseSubtask3);
        cleanHouseSubtask2.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(cleanHouseSubtask2);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : Main.inMemoryTaskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : Main.inMemoryTaskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : Main.inMemoryTaskManager.getEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : Main.inMemoryTaskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printViewHistory() {
        //просматриваем 11 задач, в истории должны отобразиться последние 10
        Main.inMemoryTaskManager.getTaskByID(1);
        Main.inMemoryTaskManager.getTaskByID(2);
        Main.inMemoryTaskManager.getEpicByID(3);
        Main.inMemoryTaskManager.getTaskByID(1);
        Main.inMemoryTaskManager.getSubtaskByID(4);
        Main.inMemoryTaskManager.getSubtaskByID(5);
        Main.inMemoryTaskManager.getSubtaskByID(6);
        Main.inMemoryTaskManager.getEpicByID(3);
        Main.inMemoryTaskManager.getSubtaskByID(4);
        Main.inMemoryTaskManager.getTaskByID(2);
        Main.inMemoryTaskManager.getSubtaskByID(6);

        System.out.println();
        System.out.println("История просмотров:");
        for (Task task : Main.inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }
    }
}