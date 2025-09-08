package com.ivangochev.raceratingapi.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceAdvancedTest {

    @Mock
    private NotificationRecipientRepository recipientRepo;

    @InjectMocks
    private NotificationService notificationService;

    @Nested
    @DisplayName("Mark Notification As Read Tests")
    class MarkNotificationAsReadTests {

        @Test
        @DisplayName("Should propagate repository exceptions")
        void shouldPropagateRepositoryExceptions() {
            // Arrange
            when(recipientRepo.markRead(any(), any()))
                    .thenThrow(new DataAccessException("Database error") {});

            // Act & Assert
            assertThrows(DataAccessException.class, () ->
                    notificationService.markNotificationAsRead(1L, 1L));
            verify(recipientRepo).markRead(1L, 1L);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 5})
        @DisplayName("Should handle different update counts")
        void shouldHandleDifferentUpdateCounts(int updateCount) {
            // Arrange
            Long userId = 1L;
            Long notificationId = 1L;
            when(recipientRepo.markRead(userId, notificationId)).thenReturn(updateCount);

            // Act
            notificationService.markNotificationAsRead(userId, notificationId);

            // Assert
            verify(recipientRepo).markRead(userId, notificationId);
        }
    }

    @Nested
    @DisplayName("Mark All Notifications As Read Tests")
    class MarkAllNotificationsAsReadTests {

        @Test
        @DisplayName("Should propagate repository exceptions")
        void shouldPropagateRepositoryExceptions() {
            // Arrange
            when(recipientRepo.markAllRead(any()))
                    .thenThrow(new DataAccessException("Database error") {});

            // Act & Assert
            assertThrows(DataAccessException.class, () ->
                    notificationService.markAllNotificationsAsRead(1L));
            verify(recipientRepo).markAllRead(1L);
        }
    }

    @Nested
    @DisplayName("Delete Notification Tests")
    class DeleteNotificationTests {

        @Test
        @DisplayName("Should propagate repository exceptions")
        void shouldPropagateRepositoryExceptions() {
            // Arrange
            when(recipientRepo.deleteByIdAndUserId(any(), any()))
                    .thenThrow(new DataAccessException("Database error") {});

            // Act & Assert
            assertThrows(DataAccessException.class, () ->
                    notificationService.deleteNotification(1L, 1L));
            verify(recipientRepo).deleteByIdAndUserId(1L, 1L);
        }

        @Test
        @DisplayName("Should handle null inputs")
        void shouldHandleNullInputs() {
            // Act & Assert
            assertThrows(NullPointerException.class, () ->
                    notificationService.deleteNotification(null, 1L));

            assertThrows(NullPointerException.class, () ->
                    notificationService.deleteNotification(1L, null));

            // Verify no repository calls happened
            verifyNoInteractions(recipientRepo);
        }
    }
}