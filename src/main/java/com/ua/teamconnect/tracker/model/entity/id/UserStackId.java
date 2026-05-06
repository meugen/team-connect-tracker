package com.ua.teamconnect.tracker.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserStackId {

    @Column(nullable = false, name = "stack_id", insertable = false, updatable = false)
    private Long stackId;

    @Column(nullable = false, name = "user_id", insertable = false, updatable = false)
    private Long userId;
}
