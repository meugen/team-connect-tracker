package com.ua.teamconnect.tracker.model.entity;

import com.ua.teamconnect.tracker.model.entity.id.UserProjectId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_project")
@Getter @Setter
public class UserProject {

    public static UserProject of(User user, Project project) {
        var userProject = new UserProject();
        userProject.setId(UserProjectId.of(user, project));
        userProject.setUser(user);
        userProject.setProject(project);
        return userProject;
    }

    @EmbeddedId
    private UserProjectId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Project project;

    @Column
    private String role;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
