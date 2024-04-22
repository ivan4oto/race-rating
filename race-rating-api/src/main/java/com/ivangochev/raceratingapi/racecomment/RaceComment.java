package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"author_id", "race_id"})
})
public class RaceComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(length = 2000, nullable = false)
    @Nullable
    private String commentText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
