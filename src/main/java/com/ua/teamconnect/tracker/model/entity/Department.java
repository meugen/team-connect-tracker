package com.ua.teamconnect.tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Getter @Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "departments_id_seq")
    @SequenceGenerator(name = "departments_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "head_id")
    private Integer headId;

}
