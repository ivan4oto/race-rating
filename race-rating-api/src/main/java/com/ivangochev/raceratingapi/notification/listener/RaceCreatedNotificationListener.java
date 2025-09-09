package com.ivangochev.raceratingapi.notification.listener;

import com.ivangochev.raceratingapi.notification.*;
import com.ivangochev.raceratingapi.notification.event.RaceCreatedEvent;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.racecomment.RaceCommentRepository;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Component
public class RaceCreatedNotificationListener {
    private final NotificationRepository notificationRepo;
    private final UserRepository userRepository;
    private final NotificationRecipientRepository recipientRepository;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRaceCreated(RaceCreatedEvent event) {
        notifyAllUsersAboutNewRace(event.getRaceId(), event.getRaceName() );
    }

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

        List<Long> userIds = userRepository.findAllIds();

        List<NotificationRecipient> batch = new ArrayList<>(userIds.size());
        for (Long uid : userIds) {
            NotificationRecipient r = new NotificationRecipient();
            r.setNotification(n);
            r.setUserId(uid);
            batch.add(r);
            if (batch.size() == 1000) { recipientRepository.saveAll(batch); batch.clear(); }
        }
        if (!batch.isEmpty()) recipientRepository.saveAll(batch);
    }
}
