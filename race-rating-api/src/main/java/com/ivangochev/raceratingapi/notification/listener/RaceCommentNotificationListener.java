package com.ivangochev.raceratingapi.notification.listener;

import com.ivangochev.raceratingapi.notification.*;
import com.ivangochev.raceratingapi.notification.event.RaceCommentCreatedEvent;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.racecomment.RaceCommentRepository;
import com.ivangochev.raceratingapi.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RaceCommentNotificationListener {
    private final RaceCommentRepository commentRepository;
    private final RaceRepository raceRepository;
    private final NotificationRepository notificationRepo;
    private final NotificationRecipientRepository recipientRepo;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(RaceCommentCreatedEvent event) {
        log.debug("New comment created event: {}", event);
        Long raceId = event.getRaceId();
        Long authorId = event.getAuthorId();
        List<User> recipients =
                commentRepository.findDistinctAuthorsByRaceIdExcludingUser(raceId, authorId);
        if (recipients.isEmpty()) {
            return;
        }
        Race race = raceRepository.findById(raceId).orElse(null);
        RaceComment comment = commentRepository.findById(event.getCommentId()).orElse(null);
        if (race == null || comment == null) {
            log.warn("Race or comment not found for new comment created event: {}", event);
            return;
        }
        for (User recipient : recipients) {
            try {
                notifyUsersAboutNewComment(recipient, race, comment);
            } catch (Exception ex) {
                log.warn("Failed to notify user {} for race {} new comment {}",
                        recipient.getId(), raceId, event.getCommentId(), ex);
            }
        }
    }

    public void notifyUsersAboutNewComment(User recipient, Race race, RaceComment newComment) {
        Notification n = new Notification();
        n.setType("NEW_COMMENT");
        n.setTitle("A new comment added to: " + race.getName());
        n.setBody(newComment.getCommentText());
        notificationRepo.save(n);
        notificationRepo.flush();
        NotificationRecipient r = new NotificationRecipient();
        r.setNotification(n);
        r.setUserId(recipient.getId());
        recipientRepo.save(r);
    }
}
