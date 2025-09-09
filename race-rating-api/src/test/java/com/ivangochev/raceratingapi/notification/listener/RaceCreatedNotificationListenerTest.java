package com.ivangochev.raceratingapi.notification.listener;

import com.ivangochev.raceratingapi.notification.Notification;
import com.ivangochev.raceratingapi.notification.NotificationRecipient;
import com.ivangochev.raceratingapi.notification.NotificationRecipientRepository;
import com.ivangochev.raceratingapi.notification.NotificationRepository;
import com.ivangochev.raceratingapi.notification.event.RaceCreatedEvent;
import com.ivangochev.raceratingapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaceCreatedNotificationListenerTest {

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRecipientRepository recipientRepository;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Captor
    private ArgumentCaptor<List<NotificationRecipient>> recipientsCaptor;

    private RaceCreatedNotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new RaceCreatedNotificationListener(notificationRepo, userRepository, recipientRepository);
    }

    @Test
    void onRaceCreated_ShouldCallNotifyAllUsersAboutNewRace() {
        // Arrange
        RaceCreatedEvent event = new RaceCreatedEvent(this, 1L, 1L, "Test Race");
        RaceCreatedNotificationListener spyListener = spy(listener);
        doNothing().when(spyListener).notifyAllUsersAboutNewRace(anyLong(), anyString());

        // Act
        spyListener.onRaceCreated(event);

        // Assert
        verify(spyListener).notifyAllUsersAboutNewRace(1L, "Test Race");
    }

    @Test
    void notifyAllUsersAboutNewRace_ShouldCreateAndSaveNotification() {
        // Arrange
        Long raceId = 1L;
        String raceName = "Test Race";

        // Act
        listener.notifyAllUsersAboutNewRace(raceId, raceName);

        // Assert
        verify(notificationRepo).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals("NEW_RACE", savedNotification.getType());
        assertEquals("A new race added to the system!", savedNotification.getTitle());
        assertEquals(raceName, savedNotification.getBody());
        assertEquals(raceId, savedNotification.getMetadataJson().get("raceId"));
        assertEquals(raceName, savedNotification.getMetadataJson().get("raceName"));
    }

    @Test
    void notifyAllUsersAboutNewRace_ShouldCreateAndSaveRecipientsForAllUsers() {
        // Arrange
        Long raceId = 1L;
        String raceName = "Test Race";
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        when(userRepository.findAllIds()).thenReturn(userIds);

        Notification notification = new Notification();
        notification.setType("NEW_RACE");
        notification.setTitle("A new race added to the system!");
        notification.setBody("Test Race");
        notification.getMetadataJson().put("raceId", raceId);
        notification.getMetadataJson().put("raceName", raceName);

        when(notificationRepo.save(any(Notification.class))).thenReturn(notification);

        // Act
        listener.notifyAllUsersAboutNewRace(raceId, raceName);

        // Assert
        verify(recipientRepository).saveAll(recipientsCaptor.capture());
        List<NotificationRecipient> savedRecipients = recipientsCaptor.getValue();

        assertEquals(3, savedRecipients.size());
        for (int i = 0; i < userIds.size(); i++) {
            assertEquals(userIds.get(i), savedRecipients.get(i).getUserId());
            assertEquals(notification, savedRecipients.get(i).getNotification());
        }
    }

    @Test
    void notifyAllUsersAboutNewRace_ShouldHandleEmptyUserList() {
        // Arrange
        Long raceId = 1L;
        String raceName = "Test Race";
        List<Long> userIds = List.of();
        when(userRepository.findAllIds()).thenReturn(userIds);

        // Act
        listener.notifyAllUsersAboutNewRace(raceId, raceName);

        // Assert
        verify(notificationRepo).save(any(Notification.class));
        verify(notificationRepo).flush();
        verify(userRepository).findAllIds();
        verify(recipientRepository, never()).saveAll(anyList());
    }
}