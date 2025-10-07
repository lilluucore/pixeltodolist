package com.todolist.japanese.dto;

import com.todolist.japanese.model.Task;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Task.Priority priority = Task.Priority.MEDIUM;
    private LocalDate dueDate;
}
