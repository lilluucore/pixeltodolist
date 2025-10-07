package com.todolist.japanese.dto;

import com.todolist.japanese.model.Task;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Task.Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static TaskResponse fromTask(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setCompletedAt(task.getCompletedAt());
        return response;
    }
}
