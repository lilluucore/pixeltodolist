package com.todolist.japanese.service;

import com.todolist.japanese.model.Task;
import com.todolist.japanese.model.User;
import com.todolist.japanese.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ActivityService activityService;

    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Task> getTasksByStatus(User user, boolean completed) {
        return taskRepository.findByUserAndCompletedOrderByCreatedAtDesc(user, completed);
    }

    @Transactional
    public Task createTask(User user, String title, String description, Task.Priority priority, LocalDate dueDate) {
        Task task = new Task(title, description, user);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, String title, String description, Task.Priority priority, LocalDate dueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        if (priority != null) task.setPriority(priority);
        task.setDueDate(dueDate);

        return taskRepository.save(task);
    }

    @Transactional
    public Task toggleTaskCompletion(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setCompleted(!task.isCompleted());

        if (task.isCompleted()) {
            task.setCompletedAt(LocalDateTime.now());
            activityService.logTaskCompletion(task.getUser());
        } else {
            task.setCompletedAt(null);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public long getActiveTaskCount(User user) {
        return taskRepository.countByUserAndCompleted(user, false);
    }

    public long getCompletedTaskCount(User user) {
        return taskRepository.countByUserAndCompleted(user, true);
    }
}
