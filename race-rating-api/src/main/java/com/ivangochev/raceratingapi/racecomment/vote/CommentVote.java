package com.ivangochev.raceratingapi.racecomment.vote;

import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class CommentVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private RaceComment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User voter;

    @Column(nullable = false)
    private boolean upvote; // true for thumbs up, false for thumbs down

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date votedAt;

    @PrePersist
    protected void onCreate() {
        this.votedAt = new Date();
    }
}
