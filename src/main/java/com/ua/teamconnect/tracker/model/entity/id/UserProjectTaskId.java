package com.ua.teamconnect.tracker.model.entity.id;

import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.Task;
import com.ua.teamconnect.tracker.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserProjectTaskId {

    public static UserProjectTaskId of(User user, Project project, Task task) {
        var id = new UserProjectTaskId();
        id.setUserId(user.getId());
        id.setProjectId(project.getId());
        id.setTaskId(task.getId());
        return id;
    }

    @Column(nullable = false, name = "user_id")
    private Integer userId;

    @Column(nullable = false, name = "project_id")
    private Integer projectId;

    @Column(nullable = false, name = "task_id")
    private Integer taskId;
}
