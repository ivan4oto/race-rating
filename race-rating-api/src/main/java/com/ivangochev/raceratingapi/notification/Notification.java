package com.ivangochev.raceratingapi.notification;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue
    Long id;
    String type;
    String title;
    @Column(columnDefinition = "TEXT")
    String body;
    @JdbcTypeCode(SqlTypes.JSON)                // <-- tells Hibernate to bind as JSON
    @Column(name = "metadata_json", columnDefinition = "jsonb")
    Map<String, Object> metadataJson = new HashMap<>();
    @CreationTimestamp
    Instant createdAt;
}
