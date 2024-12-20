package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasks;

    //Конструктор для создания новых объектов, id получаем в InMemoryTasksManager
    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    //Конструктор для загрузки данных из файла. id задается из файла
    public Epic(String name, String description, int id) {
        super(name, description, id);
        subTasks = new ArrayList<>();
    }

    //Конструктор для id
    public Epic(int id, String name, String description) {
        super(id, name, description);
        subTasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public int addSubTaskIdToEpic(Task task) {
        if (task.getId().equals(this.getId())) {
            return -1;
        }
        this.subTasks.add(task.getId());
        task.setRelationEpicId(this);
        return 1;
    }
}