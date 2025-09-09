package com.ivangochev.raceratingapi.notification;

import java.time.Instant;

public record NotificationDto(
        Long id,
        String type,
        String title,
        String body,
        String metadataJson,
        Instant createdAt,
        boolean isRead,
        Instant readAt
) {
}
