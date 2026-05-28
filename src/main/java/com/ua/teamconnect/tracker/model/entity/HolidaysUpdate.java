package com.ua.teamconnect.tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "holidays_updates")
@Getter @Setter
public class HolidaysUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "holidays_updates_id_seq")
    @SequenceGenerator(name = "holidays_updates_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
}
