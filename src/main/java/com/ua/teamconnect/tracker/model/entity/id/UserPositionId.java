package com.ua.teamconnect.tracker.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserPositionId {

    @Column(nullable = false, name = "position_id", insertable = false, updatable = false)
    private Long positionId;

    @Column(nullable = false, name = "user_id", insertable = false, updatable = false)
    private Long userId;
}
