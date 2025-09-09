package com.ivangochev.raceratingapi.notification;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {

    // Unread for a user (paged)
    Page<NotificationRecipient> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);

    // All for a user (paged)
    Page<NotificationRecipient> findByUserId(Long userId, Pageable pageable);

    long deleteByIdAndUserId(Long id, Long userId);

    // Fast badge count
    long countByUserIdAndIsReadFalse(Long userId);

    // Mark single item as read (ownership-checked)
    @Modifying
    @Query("""
                update NotificationRecipient r
                   set r.isRead = true, r.readAt = CURRENT_TIMESTAMP
                 where r.id = :recipientId and r.userId = :userId and r.isRead = false
            """)
    int markRead(Long userId, Long recipientId);

    // Mark all unread as read
    @Modifying
    @Query("""
                update NotificationRecipient r
                   set r.isRead = true, r.readAt = CURRENT_TIMESTAMP
                 where r.userId = :userId and r.isRead = false
            """)
    int markAllRead(Long userId);


    // Optional cleanup (e.g., purge read items older than X)
    @Modifying
    @Query("""
                delete from NotificationRecipient r
                 where r.isRead = true and r.readAt < :threshold
            """)
    int deleteReadBefore(Instant threshold);


    // ---- Advanced (optional) ----
    // Ultra-fast fan-out using a set-based insert (PostgreSQL). Requires nativeQuery.
    // Useful when notifying *all active users* without iterating in Java.
    @Modifying
    @Query(value = """
                INSERT INTO notification_recipient (notification_id, user_id, is_read, is_hidden)
                SELECT :notificationId, u.id, false, false
                FROM users u
            """, nativeQuery = true)
    int fanOutToAllActiveUsers(long notificationId);
}
