package com.ua.teamconnect.tracker.model.entity.id;

import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserProjectId implements Serializable {

    public static UserProjectId of(User user, Project project) {
        var id = new UserProjectId();
        id.setUserId(user.getId());
        id.setProjectId(project.getId());
        return id;
    }

    @Column(nullable = false, name = "user_id")
    private Integer userId;

    @Column(nullable = false, name = "project_id")
    private Integer projectId;
}
