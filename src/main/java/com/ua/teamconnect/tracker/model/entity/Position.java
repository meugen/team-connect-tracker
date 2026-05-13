package com.ua.teamconnect.tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "positions")
@Getter @Setter
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "positions_id_seq")
    @SequenceGenerator(name = "positions_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "department_id", insertable = false, updatable = false)
    private Integer departmentId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

}
