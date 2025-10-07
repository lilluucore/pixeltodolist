package com.todolist.japanese.controller;

import com.todolist.japanese.dto.DashboardStats;
import com.todolist.japanese.model.User;
import com.todolist.japanese.service.ActivityService;
import com.todolist.japanese.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final TaskService taskService;
    private final ActivityService activityService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;

        long activeTasks = taskService.getActiveTaskCount(user);
        long completedTasks = taskService.getCompletedTaskCount(user);
        int streak = activityService.calculateStreak(user);

        DashboardStats stats = new DashboardStats(activeTasks, completedTasks, streak);
        return ResponseEntity.ok(stats);
    }
}
