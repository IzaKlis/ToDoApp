package com.example.todoapp;

import java.util.ArrayList;
import java.util.UUID;

public class TaskStorage {
    private static TaskStorage instance = new TaskStorage();
    private final ArrayList<Task> tasks;
    private TaskStorage() {
        tasks = new ArrayList<>();
        for (int i = 1; i <=5 ; ++i) {
            Task task = new Task();
            task.setName("Zadanie nr " + i);
            task.setDone(i % 3 == 0);
            tasks.add(task);
            if(i%3==0){
                task.setCategory(Category.STUDIES);
            }else{
                task.setCategory(Category.HOME);
            }
        }
    }

    public static TaskStorage getInstance() {
        if(instance == null) {
            instance = new TaskStorage();
        }
        return instance;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public Task getTask(UUID id) {
        for (Task task : tasks) {
            if (task.getId().equals(id))
                return task;
        }
        return null;
    }
    public void addTask(Task task){
        tasks.add(task);
    }
}
