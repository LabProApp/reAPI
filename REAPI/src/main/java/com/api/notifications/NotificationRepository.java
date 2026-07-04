package com.api.notifications;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetries);

    // Picks up FAILED records that have cooled down AND RETRYING records stuck
    // longer than one interval (app crashed mid-retry or unexpected exception).
    @Query("SELECT n FROM Notification n WHERE n.status IN ('FAILED', 'RETRYING') AND n.retryCount < :maxRetries AND n.updatedAt < :before")
    List<Notification> findRetryable(
        @Param("maxRetries") int maxRetries,
        @Param("before") LocalDateTime before
    );

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);

    List<Notification> findByNotificationTypeAndStatusOrderByCreatedAtDesc(String type, NotificationStatus status);
}
