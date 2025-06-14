package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVote;
import com.ivangochev.raceratingapi.rating.Rating;
import com.ivangochev.raceratingapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "races")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ratings_count", nullable = false)
    private int ratingsCount = 0;

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

    @Column(precision = 10, scale = 6) // Precision for decimal degrees
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6) // Precision for decimal degrees
    private BigDecimal longitude;

    private String websiteUrl;

    private String logoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date eventDate;

    @ManyToOne()
    @JoinColumn(name = "created_by_user_id")
    private User author;

    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RaceComment> raceComments;
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    // inverse side of the “votedForRaces” relationship
    @ManyToMany(mappedBy = "votedForRaces")
    private List<User> voters = new ArrayList<>();

    // inverse side of the “commentedForRaces” relationship
    @ManyToMany(mappedBy = "commentedForRaces")
    private List<User> commenters = new ArrayList<>();
}
