package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "races")
public class Race {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(precision = 2, scale = 1,nullable = false)
    private BigDecimal averageRating;

    @Column(precision = 2, scale = 1,nullable = false)
    private BigDecimal averageTraceScore;
    @Column(precision = 2, scale = 1,nullable = false)
    private BigDecimal averageVibeScore;
    @Column(precision = 2, scale = 1,nullable = false)
    private BigDecimal averageOrganizationScore;
    @Column(precision = 2, scale = 1,nullable = false)
    private BigDecimal averageLocationScore;
    @Column(precision = 2, scale = 1,nullable = false)
    private BigDecimal averageValueScore;

    private Integer numberOfVoters;

    @Column(precision = 10, scale = 6) // Precision for decimal degrees
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6) // Precision for decimal degrees
    private BigDecimal longitude;

    private String websiteUrl;

    private String logoUrl;

    @Column(nullable = false)
    private String terrain; // track, road, trail, big mountain

    @Column(nullable = false)
    private BigDecimal distance;

    @Column(nullable = false)
    private Integer elevation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date eventDate;

    @ManyToOne()
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

}
