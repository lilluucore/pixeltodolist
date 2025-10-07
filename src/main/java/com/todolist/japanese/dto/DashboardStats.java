package com.todolist.japanese.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStats {
    private long activeTasks;
    private long completedTasks;
    private int currentStreak;
}
