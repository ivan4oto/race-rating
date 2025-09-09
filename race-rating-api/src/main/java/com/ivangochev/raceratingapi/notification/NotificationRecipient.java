package com.ivangochev.raceratingapi.notification;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class NotificationRecipient {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(fetch = FetchType.LAZY) Notification notification;

    Long userId;
    boolean isRead = false;
    Instant readAt;
    boolean isHidden = false;
}