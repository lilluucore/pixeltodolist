package com.todolist.japanese.repository;

import com.todolist.japanese.model.ActivityLog;
import com.todolist.japanese.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Optional<ActivityLog> findByUserAndDate(User user, LocalDate date);

    @Query("SELECT a FROM ActivityLog a WHERE a.user = :user AND a.date >= :startDate ORDER BY a.date ASC")
    List<ActivityLog> findRecentActivity(User user, LocalDate startDate);

    List<ActivityLog> findByUserOrderByDateDesc(User user);
}
