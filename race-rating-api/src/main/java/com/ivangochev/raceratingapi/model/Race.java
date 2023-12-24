package com.ivangochev.raceratingapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    @Column(precision = 10, scale = 6) // Precision for decimal degrees
    private BigDecimal latitude;
    @Column(precision = 10, scale = 6) // Precision for decimal degrees
    private BigDecimal longitude;
}
