package com.ivangochev.raceratingapi.events;

import com.ivangochev.raceratingapi.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RaceEventsListener {
    private final NotificationService notifications;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRaceCreated(RaceCreatedEvent e) {
        try {
            notifications.notifyAllUsersAboutNewRace(e.raceId(), e.name());
        } catch (Exception ex) {
            log.error("Post-commit notification failed for raceId={}", e.raceId(), ex);
        }
    }
}
