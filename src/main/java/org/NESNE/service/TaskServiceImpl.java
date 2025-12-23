package org.NESNE.service;

import org.NESNE.exception.TaskNotFoundException;
import org.NESNE.model.Priority;
import org.NESNE.model.Task;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskServiceImpl implements TaskService {

    private void loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");

                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                Priority priority = Priority.valueOf(parts[2]);
                boolean completed = parts[3].equals("DONE");

                Task task = new Task(id, title, priority);
                if (completed) {
                    task.markCompleted();
                }

                tasks.add(task);
                counter = Math.max(counter, id + 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TaskServiceImpl() {
        loadFromFile();
    }

    private List<Task> tasks = new ArrayList<>();
    private int counter = 1;
    private final String FILE_PATH = "src/main/resources/List.txt";

    @Override
    public void addTask(String title, String description,
                        Priority priority, LocalDate deadline) {

        Task task = new Task(counter++, title, description, priority, deadline);
        tasks.add(task);
        saveToFile();
    }


    @Override
    public void completeTask(int id) throws TaskNotFoundException {
        Task task = findById(id);
        task.markCompleted();
        saveToFile();
    }

    @Override
    public void deleteTask(int id) throws TaskNotFoundException {
        Task task = findById(id);
        tasks.remove(task);
        saveToFile();
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks;
    }

    private Task findById(int id) throws TaskNotFoundException {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found: " + id));
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Task t : tasks) {
                pw.println(
                        t.getId() + " | " +
                                t.getTitle() + " | " +
                                t.getDescription() + " | " +
                                t.getPriority() + " | " +
                                t.getDeadline() + " | " +
                                (t.isCompleted() ? "DONE" : "TODO")
                );

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
