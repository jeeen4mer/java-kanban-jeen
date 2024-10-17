import task.*;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task buyProducts = new Task("Купить продукты", "Вместе с печеньем");
        Task buyProductsCreated = taskManager.addTask(buyProducts);
        System.out.println(buyProductsCreated);

        Task buyProductsToUpdate = new Task(buyProducts.getId(), "Не забыть купить продукты", "Можно без печенья",
                Status.IN_PROGRESS);
        Task buyProductsUpdated = taskManager.updateTask(buyProductsToUpdate);
        System.out.println(buyProductsUpdated);

        Task cleanWindows = new Task("Помыть окна", "Роботом для мойки окон");
        Task cleanWindowsCreated = taskManager.addTask(cleanWindows);
        System.out.println(cleanWindowsCreated);


        Epic cleanHouse = new Epic("Прибраться в доме", "Выбросить ненужные вещи");
        taskManager.addEpic(cleanHouse);
        System.out.println(cleanHouse);
        Subtask cleanHouseSubtask1 = new Subtask("Протереть пыль", "В том числе за диваном",
                cleanHouse.getId());
        Subtask cleanHouseSubtask2 = new Subtask("Пропылесосить", "Новым пылесосом",
                cleanHouse.getId());
        taskManager.addSubtask(cleanHouseSubtask1);
        taskManager.addSubtask(cleanHouseSubtask2);
        System.out.println(cleanHouse);
        taskManager.updateSubtask(cleanHouseSubtask2);
        System.out.println(cleanHouse);


        Epic fixWaterTap = new Epic("Починить кран", "В ванной");
        taskManager.addEpic(fixWaterTap);
        System.out.println(fixWaterTap);
        Subtask fixWaterTapSubtask1 = new Subtask("Купить новый кран", "Установить новый кран",
                fixWaterTap.getId());
        taskManager.addSubtask(fixWaterTapSubtask1);
        System.out.println(fixWaterTap);

        System.out.println();
        System.out.println("Изменяем статусы и удаляем одну из задач, и один из эпиков");
        System.out.println();

        buyProducts.setStatus(Status.IN_PROGRESS);
        System.out.println(buyProductsCreated);
        buyProducts.setStatus(Status.DONE);
        System.out.println(buyProductsCreated);

        System.out.println();

        cleanHouse.setStatus(Status.IN_PROGRESS);
        System.out.println(cleanHouse);
        cleanHouseSubtask2.setStatus(Status.DONE);
        cleanHouseSubtask1.setStatus(Status.IN_PROGRESS);

        System.out.println();

        taskManager.deleteTaskByID(cleanWindows.getId());
        System.out.println(taskManager.tasks);
        taskManager.deleteEpicByID(fixWaterTap.getId());
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);
    }
}