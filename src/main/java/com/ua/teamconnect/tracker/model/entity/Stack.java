package com.ua.teamconnect.tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stacks")
@Getter @Setter
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "stacks_id_seq")
    @SequenceGenerator(name = "stacks_id_seq", allocationSize = 1)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;

}
