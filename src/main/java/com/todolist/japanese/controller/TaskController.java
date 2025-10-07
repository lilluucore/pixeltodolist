package com.todolist.japanese.controller;

import com.todolist.japanese.dto.TaskRequest;
import com.todolist.japanese.dto.TaskResponse;
import com.todolist.japanese.model.Task;
import com.todolist.japanese.model.User;
import com.todolist.japanese.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String filter) {

        User user = (User) userDetails;
        List<Task> tasks;

        if ("active".equals(filter)) {
            tasks = taskService.getTasksByStatus(user, false);
        } else if ("completed".equals(filter)) {
            tasks = taskService.getTasksByStatus(user, true);
        } else {
            tasks = taskService.getAllTasksByUser(user);
        }

        List<TaskResponse> response = tasks.stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TaskRequest request) {

        User user = (User) userDetails;
        Task task = taskService.createTask(
                user,
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate()
        );

        return ResponseEntity.ok(TaskResponse.fromTask(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request) {

        Task task = taskService.updateTask(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate()
        );

        return ResponseEntity.ok(TaskResponse.fromTask(task));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<TaskResponse> toggleTask(@PathVariable Long id) {
        Task task = taskService.toggleTaskCompletion(id);
        return ResponseEntity.ok(TaskResponse.fromTask(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
