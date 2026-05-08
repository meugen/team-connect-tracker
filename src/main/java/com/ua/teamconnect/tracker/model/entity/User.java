package com.ua.teamconnect.tracker.model.entity;

import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.model.pojo.converter.GenderConverter;
import com.ua.teamconnect.tracker.model.pojo.converter.PhoneConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "employees_id_seq")
    @SequenceGenerator(name = "employees_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column
    private String avatar;

    @Convert(converter = PhoneConverter.class)
    @Column(nullable = false)
    private Map<String, String> phone;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Convert(converter = GenderConverter.class)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String grade;
}
