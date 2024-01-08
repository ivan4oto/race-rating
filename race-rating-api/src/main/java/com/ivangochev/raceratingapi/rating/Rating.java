package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ratings")
public class Rating implements Serializable {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private int traceScore; // 1 to 5

    @Column(nullable = false)
    private int vibeScore; // 1 to 5

    @Column(nullable = false)
    private int organizationScore; // 1 to 5

    @Column(nullable = false)
    private int locationScore; // 1 to 5

    @Column(nullable = false)
    private int valueScore; // 1 to 5

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;


    @PrePersist
    public void onPrePersist() {
        createdAt = new Date();
    }
}
