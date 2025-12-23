package org.NESNE.service;

import org.NESNE.exception.TaskNotFoundException;
import org.NESNE.model.Priority;
import org.NESNE.model.Task;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskServiceImpl implements TaskService {

    private final String FILE_PATH = "src/main/resources/List.txt";
    private final List<Task> tasks = new ArrayList<>();
    private int counter = 1;

    public TaskServiceImpl() {
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Dosya oluşturulamadı: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(" \\| ");

                if (parts.length < 6) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    String description = parts[2].trim();
                    Priority priority = Priority.valueOf(parts[3].trim());
                    LocalDate deadline = LocalDate.parse(parts[4].trim());
                    boolean completed = parts[5].trim().equals("DONE");

                    Task task = new Task(id, title, description, priority, deadline);

                    if (completed) {
                        task.markCompleted();
                    }

                    tasks.add(task);
                    counter = Math.max(counter, id + 1);

                } catch (Exception e) {
                    System.err.println("Satır okunurken hata: " + line + " -> " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                String safeDescription = t.getDescription()
                        .replace("\n", " ")
                        .replace("|", "-");

                pw.println(
                        t.getId() + " | " +
                                t.getTitle() + " | " +
                                safeDescription + " | " +
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
