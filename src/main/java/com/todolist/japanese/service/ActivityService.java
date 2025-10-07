package com.todolist.japanese.service;

import com.todolist.japanese.model.ActivityLog;
import com.todolist.japanese.model.User;
import com.todolist.japanese.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void logTaskCompletion(User user) {
        LocalDate today = LocalDate.now();
        ActivityLog log = activityLogRepository.findByUserAndDate(user, today)
                .orElse(new ActivityLog(user, today));

        log.setTasksCompleted(log.getTasksCompleted() + 1);
        activityLogRepository.save(log);
    }

    public int calculateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        int streak = 0;

        List<ActivityLog> recentLogs = activityLogRepository.findRecentActivity(user, today.minusDays(365));

        for (ActivityLog log : recentLogs) {
            if (log.getDate().equals(checkDate) && log.getTasksCompleted() > 0) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else if (log.getDate().isBefore(checkDate)) {
                break;
            }
        }

        List<ActivityLog> allLogs = activityLogRepository.findByUserOrderByDateDesc(user);
        for (ActivityLog log : allLogs) {
            if (log.getDate().equals(checkDate) && log.getTasksCompleted() > 0) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else if (log.getDate().isBefore(checkDate)) {
                break;
            }
        }

        return streak;
    }

    public List<ActivityLog> getRecentActivity(User user, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return activityLogRepository.findRecentActivity(user, startDate);
    }
}
