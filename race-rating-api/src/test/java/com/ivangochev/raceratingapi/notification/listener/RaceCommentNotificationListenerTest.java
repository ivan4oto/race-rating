package com.ivangochev.raceratingapi.notification.listener;

import com.ivangochev.raceratingapi.notification.*;
import com.ivangochev.raceratingapi.notification.event.RaceCommentCreatedEvent;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.racecomment.RaceCommentRepository;
import com.ivangochev.raceratingapi.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RaceCommentNotificationListenerTest {

    @Mock
    private RaceCommentRepository commentRepository;

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private NotificationRecipientRepository recipientRepo;

    @InjectMocks
    private RaceCommentNotificationListener listener;

    private Race race;
    private RaceComment comment;
    private User author;
    private User recipient1;
    private User recipient2;
    private RaceCommentCreatedEvent event;

    @BeforeEach
    void setUp() {
        // Setup test data
        race = new Race();
        race.setId(1L);
        race.setName("Test Race");

        author = new User();
        author.setId(1L);
        author.setUsername("author");

        recipient1 = new User();
        recipient1.setId(2L);
        recipient1.setUsername("recipient1");

        recipient2 = new User();
        recipient2.setId(3L);
        recipient2.setUsername("recipient2");

        comment = new RaceComment();
        comment.setId(1L);
        comment.setRace(race);
        comment.setCommentText("Test comment");
        comment.setAuthor(author);

        event = new RaceCommentCreatedEvent(this, 1L, 1L, 1L);
    }

    @Test
    void onCommentCreated_WithValidRecipientsAndData_ShouldCreateNotifications() {
        // Arrange
        List<User> recipients = Arrays.asList(recipient1, recipient2);

        when(commentRepository.findDistinctAuthorsByRaceIdExcludingUser(1L, 1L)).thenReturn(recipients);
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        listener.onCommentCreated(event);

        // Assert
        verify(notificationRepo, times(2)).save(any(Notification.class));
        verify(notificationRepo, times(2)).flush();
        verify(recipientRepo, times(2)).save(any(NotificationRecipient.class));
    }

    @Test
    void onCommentCreated_WithNoRecipients_ShouldNotCreateNotifications() {
        // Arrange
        when(commentRepository.findDistinctAuthorsByRaceIdExcludingUser(1L, 1L))
                .thenReturn(Collections.emptyList());

        // Act
        listener.onCommentCreated(event);

        // Assert
        verify(raceRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(notificationRepo, never()).save(any(Notification.class));
        verify(recipientRepo, never()).save(any(NotificationRecipient.class));
    }

    @Test
    void onCommentCreated_WithMissingRace_ShouldNotCreateNotifications() {
        // Arrange
        List<User> recipients = Arrays.asList(recipient1, recipient2);

        when(commentRepository.findDistinctAuthorsByRaceIdExcludingUser(1L, 1L)).thenReturn(recipients);
        when(raceRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        listener.onCommentCreated(event);

        // Assert
        verify(notificationRepo, never()).save(any(Notification.class));
        verify(recipientRepo, never()).save(any(NotificationRecipient.class));
    }

    @Test
    void onCommentCreated_WithMissingComment_ShouldNotCreateNotifications() {
        // Arrange
        List<User> recipients = Arrays.asList(recipient1, recipient2);

        when(commentRepository.findDistinctAuthorsByRaceIdExcludingUser(1L, 1L)).thenReturn(recipients);
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        listener.onCommentCreated(event);

        // Assert
        verify(notificationRepo, never()).save(any(Notification.class));
        verify(recipientRepo, never()).save(any(NotificationRecipient.class));
    }

    @Test
    void onCommentCreated_WhenExceptionThrownForOneRecipient_ShouldContinueWithOthers() {
        // Arrange
        List<User> recipients = Arrays.asList(recipient1, recipient2);

        when(commentRepository.findDistinctAuthorsByRaceIdExcludingUser(1L, 1L)).thenReturn(recipients);
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Set up the first call to throw an exception
        doThrow(new RuntimeException("Test exception"))
                .doNothing()
                .when(notificationRepo).flush();

        // Act
        listener.onCommentCreated(event);

        // Assert
        // We should still try to save the second notification
        verify(notificationRepo, times(2)).save(any(Notification.class));
    }

    @Test
    void notifyUsersAboutNewComment_ShouldCreateCorrectNotification() {
        // Arrange
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        ArgumentCaptor<NotificationRecipient> recipientCaptor = ArgumentCaptor.forClass(NotificationRecipient.class);

        // Act
        listener.notifyUsersAboutNewComment(recipient1, race, comment);

        // Assert
        verify(notificationRepo).save(notificationCaptor.capture());
        verify(recipientRepo).save(recipientCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        NotificationRecipient savedRecipient = recipientCaptor.getValue();

        // Verify notification content
        assertEquals("NEW_COMMENT", savedNotification.getType());
        assertEquals("A new comment added to: Test Race", savedNotification.getTitle());
        assertEquals("Test comment", savedNotification.getBody());

        // Verify recipient setup
        assertEquals(savedNotification, savedRecipient.getNotification());
        assertEquals(recipient1.getId(), savedRecipient.getUserId());
    }
}