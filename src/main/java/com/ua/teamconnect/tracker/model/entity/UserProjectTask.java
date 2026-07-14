package com.ua.teamconnect.tracker.model.entity;

import com.ua.teamconnect.tracker.model.entity.id.UserProjectTaskId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_project_task")
@Getter @Setter
public class UserProjectTask {

    public static UserProjectTask of(User user, Project project, Task task) {
        var userProjectTask = new UserProjectTask();
        userProjectTask.setId(UserProjectTaskId.of(user, project, task));
        userProjectTask.setUser(user);
        userProjectTask.setProject(project);
        userProjectTask.setTask(task);
        return userProjectTask;
    }

    @EmbeddedId
    private UserProjectTaskId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Task task;
}
