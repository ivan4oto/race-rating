package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class RaceComment {

    @Id
    private Long id;

    private Long raceId;

    @Column(length = 2000)
    private String commentText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
