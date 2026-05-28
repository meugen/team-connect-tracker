package com.ua.teamconnect.tracker.model.entity.id;

import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserPositionId implements Serializable {

    public static UserPositionId of(User user, Position position) {
        var id = new UserPositionId();
        id.setUserId(user.getId());
        id.setPositionId(position.getId());
        return id;
    }

    @Column(nullable = false, name = "user_id")
    private Integer userId;

    @Column(nullable = false, name = "position_id")
    private Integer positionId;
}
