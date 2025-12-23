package org.NESNE.service;

import org.NESNE.model.Priority;
import org.NESNE.model.Task;
import org.NESNE.exception.TaskNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {

    void addTask(String title, String description,
                 Priority priority, LocalDate deadline);

    void completeTask(int id) throws TaskNotFoundException;

    void deleteTask(int id) throws TaskNotFoundException;

    List<Task> getAllTasks();
}

