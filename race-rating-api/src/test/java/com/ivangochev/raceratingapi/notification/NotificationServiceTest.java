package com.ivangochev.raceratingapi.notification;

import com.ivangochev.raceratingapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private NotificationRecipientRepository recipientRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Captor
    private ArgumentCaptor<List<NotificationRecipient>> batchCaptor;

    private Long raceId;
    private String raceName;
    private Long userId;
    private Long notificationRecipientId;

    @BeforeEach
    void setUp() {
        raceId = 1L;
        raceName = "Berlin Marathon";
        userId = 100L;
        notificationRecipientId = 200L;
    }

    @Test
    void notifyAllUsersAboutNewRace_ShouldCreateNotificationAndRecipients() {
        // Arrange
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        when(userRepo.findAllIds()).thenReturn(userIds);

        // Act
        notificationService.notifyAllUsersAboutNewRace(raceId, raceName);

        // Assert
        verify(notificationRepo).save(notificationCaptor.capture());
        verify(notificationRepo).flush();
        verify(recipientRepo).saveAll(any());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals("NEW_RACE", capturedNotification.getType());
        assertEquals("A new race added to the system!", capturedNotification.getTitle());
        assertEquals(raceName, capturedNotification.getBody());
        assertEquals(raceId, capturedNotification.getMetadataJson().get("raceId"));
        assertEquals(raceName, capturedNotification.getMetadataJson().get("raceName"));
    }

    @Test
    void notifyAllUsersAboutNewRace_WithMoreThan1000Users_ShouldBatchSave() {
        // Arrange
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            userIds.add((long) i);
        }
        when(userRepo.findAllIds()).thenReturn(userIds);

        // Act
        notificationService.notifyAllUsersAboutNewRace(raceId, raceName);

        // Assert
        verify(notificationRepo).save(any(Notification.class));
        verify(notificationRepo).flush();
        // Should call saveAll twice: once for the first 1000 users, once for the remaining 500
        verify(recipientRepo, times(2)).saveAll(any());
    }

    @Test
    void markNotificationAsRead_ShouldCallRepositoryMethod() {
        // Arrange
        when(recipientRepo.markRead(userId, notificationRecipientId)).thenReturn(1);

        // Act
        notificationService.markNotificationAsRead(userId, notificationRecipientId);

        // Assert
        verify(recipientRepo).markRead(userId, notificationRecipientId);
    }

    @Test
    void markAllNotificationsAsRead_ShouldCallRepositoryMethod() {
        // Arrange
        when(recipientRepo.markAllRead(userId)).thenReturn(5);

        // Act
        notificationService.markAllNotificationsAsRead(userId);

        // Assert
        verify(recipientRepo).markAllRead(userId);
    }

    @Test
    void deleteNotification_WhenNotificationExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(recipientRepo.deleteByIdAndUserId(notificationRecipientId, userId)).thenReturn(1L);

        // Act
        notificationService.deleteNotification(userId, notificationRecipientId);

        // Assert
        verify(recipientRepo).deleteByIdAndUserId(notificationRecipientId, userId);
    }

    @Test
    void deleteNotification_WhenNotificationDoesNotExist_ShouldHandleGracefully() {
        // Arrange
        when(recipientRepo.deleteByIdAndUserId(notificationRecipientId, userId)).thenReturn(0L);

        // Act
        notificationService.deleteNotification(userId, notificationRecipientId);

        // Assert
        verify(recipientRepo).deleteByIdAndUserId(notificationRecipientId, userId);
    }

    @Test
    void notifyAllUsersAboutNewRace_WithEmptyUserList_ShouldNotSaveRecipients() {
        // Arrange
        when(userRepo.findAllIds()).thenReturn(new ArrayList<>());

        // Act
        notificationService.notifyAllUsersAboutNewRace(raceId, raceName);

        // Assert
        verify(notificationRepo).save(any(Notification.class));
        verify(notificationRepo).flush();
        verify(recipientRepo, never()).saveAll(any());
    }
}