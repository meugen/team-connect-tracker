package com.ua.teamconnect.tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter @Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "tasks_id_seq")
    @SequenceGenerator(name = "tasks_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;
}
