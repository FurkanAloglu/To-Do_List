package org.NESNE.model;

import java.time.LocalDate;

public class Task {

    private int id;
    private String title;
    private String description;
    private Priority priority;
    private boolean completed;
    private LocalDate createdDate;
    private LocalDate deadline;

    public Task(int id, String title, String description,
                Priority priority, LocalDate deadline) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.createdDate = LocalDate.now();
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void markCompleted() {
        this.completed = true;
    }

    @Override
    public String toString() {
        return id + " | " + title + " | " + priority +
                " | Due: " + deadline +
                (completed ? " | DONE" : "");
    }
}
