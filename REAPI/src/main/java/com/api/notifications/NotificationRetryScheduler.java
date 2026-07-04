package com.api.notifications;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically retries FAILED notifications that have not yet hit the max-attempts cap.
 *
 * <p>Only notifications whose {@code updated_at} is older than one retry-interval are
 * picked up — this prevents immediately re-attempting a freshly failed send.</p>
 */
@Component
public class NotificationRetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationRetryScheduler.class);

    @Value("${notification.retry.max-attempts:5}")
    private int maxAttempts;

    @Value("${notification.retry.interval-ms:900000}")
    private long retryIntervalMs;

    private final NotificationRepository repo;
    private final NotificationDispatcher dispatcher;

    public NotificationRetryScheduler(NotificationRepository repo,
            NotificationDispatcher dispatcher) {
        this.repo = repo;
        this.dispatcher = dispatcher;
    }

    @Scheduled(fixedDelayString = "${notification.retry.interval-ms:900000}")
    public void retryFailedNotifications() {
        LocalDateTime cooldown = LocalDateTime.now().minusSeconds(retryIntervalMs / 1000);
        // Picks up FAILED records (normal retry) and RETRYING records stuck longer
        // than one interval (app crash or unexpected exception during previous retry).
        List<Notification> candidates = repo.findRetryable(maxAttempts, cooldown);

        if (candidates.isEmpty()) return;

        log.info("NotificationRetryScheduler - retrying {} notifications", candidates.size());
        for (Notification n : candidates) {
            log.info("NotificationRetryScheduler - retry notificationId={}, type={}, channel={}, attempt={}",
                    n.getId(), n.getNotificationType(), n.getChannel(), n.getRetryCount() + 1);
            // retry() is synchronous — status/retryCount are updated on this thread before we check them
            dispatcher.retry(n);

            // Cancel exhausted records after retry incremented retryCount
            if (n.getStatus() == NotificationStatus.FAILED && n.getRetryCount() >= maxAttempts) {
                n.markCancelled();
                repo.save(n);
                log.warn("NotificationRetryScheduler - cancelled notificationId={} after {} attempts",
                        n.getId(), n.getRetryCount());
            }
        }
    }
}
