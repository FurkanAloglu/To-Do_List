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

    // MEVCUT CONSTRUCTOR: Tam veri (UI'dan gelen) ile oluştururken kullanılır.
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

    // YENİ EKLENEN CONSTRUCTOR: Dosyadan (List.txt) okurken kullanılır.
    // Dosyada description ve deadline tutmadığın için onları varsayılan (default) değerlerle dolduruyoruz.
    public Task(int id, String title, Priority priority) {
        // Description için boş string, Deadline için "yarın" atadık.
        // Hata vermemesi için geçici bir çözüm, ama en azından çalışır.
        this(id, title, "", priority, LocalDate.now().plusDays(1));
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