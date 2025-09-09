package com.ivangochev.raceratingapi.notification;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // List all notifications (e.g., admin/history) with pagination
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);


    // Optional cleanup (delete old system-wide notifications)
    long deleteByCreatedAtBefore(Instant threshold);
}