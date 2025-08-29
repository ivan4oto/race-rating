package com.ivangochev.raceratingapi.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationQueryService {
    private final NotificationRecipientRepository recipientRepo;


    public Page<NotificationDto> listForUser(Long userId, boolean unread, int page, int size) {
        log.debug("Listing notifications for user {} (unread={})", userId, unread);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<NotificationRecipient> pr =
                unread
                        ? recipientRepo.findByUserIdAndIsReadFalse(userId, pageable)
                        : recipientRepo.findByUserId(userId, pageable);

        return pr.map(r -> new NotificationDto(
                r.getId(),
                r.getNotification().getType(),
                r.getNotification().getTitle(),
                r.getNotification().getBody(),
                r.getNotification().getMetadataJson().toString(),
                r.getNotification().getCreatedAt(),
                r.isRead(),
                r.getReadAt()
        ));
    }

}
