package com.ivangochev.raceratingapi.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRecipientRepository recipientRepo;

    @Transactional
    public void markNotificationAsRead(Long userId, Long notificationRecipientId) {
        log.debug("Marking notification {} for user {} as read", notificationRecipientId, userId);
        int updated = recipientRepo.markRead(userId, notificationRecipientId);
        log.debug("Marked {} notifications for user {} as read", updated, userId);
    }

    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        log.debug("Marking all notifications for user {} as read", userId);
        int updated = recipientRepo.markAllRead(userId);
        log.debug("Marked {} notifications for user {} as read", updated, userId);
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationRecipientId) {
        log.debug("Deleting notification {} for user {} as read", notificationRecipientId, userId);
        if (userId == null || notificationRecipientId == null) {
            throw new NullPointerException("userId and notificationRecipientId must not be null");
        }
        long numberOfRowsDeleted = recipientRepo.deleteByIdAndUserId(notificationRecipientId, userId);
        if (numberOfRowsDeleted == 0) {
            log.warn("deleteNotification: no notification found for user {} and id {}", userId, notificationRecipientId);
        }
    }
}
