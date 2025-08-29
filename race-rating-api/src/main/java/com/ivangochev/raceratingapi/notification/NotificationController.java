package com.ivangochev.raceratingapi.notification;

import com.ivangochev.raceratingapi.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService query;
    private final NotificationService notificationService;
    private final NotificationRecipientRepository recipientRepo;

    @GetMapping("/notifications")
    public Page<NotificationDto> list(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestParam(defaultValue = "true") boolean unread,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return query.listForUser(userDetails.getId(), unread, page, size);
    }

    @PostMapping("/notifications/{recipientId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Long recipientId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markNotificationAsRead(userDetails.getId(), recipientId);
    }

    @PostMapping("/notifications/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllNotificationsAsRead(userDetails.getId());
    }

    @DeleteMapping("/notifications/{recipientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long recipientId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.deleteNotification(userDetails.getId(), recipientId);
    }
}
