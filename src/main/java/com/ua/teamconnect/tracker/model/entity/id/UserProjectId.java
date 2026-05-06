package com.ua.teamconnect.tracker.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserProjectId {

    @Column(nullable = false, name = "project_id", insertable = false, updatable = false)
    private Long projectId;

    @Column(nullable = false, name = "user_id", insertable = false, updatable = false)
    private Long userId;
}
