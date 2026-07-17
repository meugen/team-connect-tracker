package com.ua.teamconnect.tracker.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "time_logs")
@Getter @Setter
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "time_logs_id_seq")
    @SequenceGenerator(name = "time_logs_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id"),
        @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    })
    private UserProjectTask userProjectTask;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, name = "duration_minutes")
    private Integer durationMinutes;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id", referencedColumnName = "id")
    private MediaFile mediaFile;
}
