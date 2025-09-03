package com.ivangochev.raceratingapi.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final NotificationRecipientRepository recipientRepo;
    private final UserRepository userRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyAllUsersAboutNewRace(Long raceId, String raceName) {
        log.info("Notifying all users about new race {} (id={})", raceName, raceId);
        Notification n = new Notification();
        n.setType("NEW_RACE");
        n.setTitle("A new race added to the system!");
        n.setBody(raceName);
        n.getMetadataJson().put("raceId", raceId);
        n.getMetadataJson().put("raceName", raceName);
        notificationRepo.save(n);
        notificationRepo.flush();

        List<Long> userIds = userRepo.findAllIds();

        List<NotificationRecipient> batch = new ArrayList<>(userIds.size());
        for (Long uid : userIds) {
            NotificationRecipient r = new NotificationRecipient();
            r.setNotification(n);
            r.setUserId(uid);
            batch.add(r);
            if (batch.size() == 1000) { recipientRepo.saveAll(batch); batch.clear(); }
        }
        if (!batch.isEmpty()) recipientRepo.saveAll(batch);
    }

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
        long numberOfRowsDeleted = recipientRepo.deleteByIdAndUserId(notificationRecipientId, userId);
        if (numberOfRowsDeleted == 0) {
            log.warn("deleteNotification: no notification found for user {} and id {}", userId, notificationRecipientId);
        }
    }
}
