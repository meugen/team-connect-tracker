package com.ua.teamconnect.tracker.model.entity;

import com.ua.teamconnect.tracker.model.entity.id.UserPositionId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_position")
@Getter @Setter
public class UserPosition {

    public static UserPosition of(User user, Position position) {
        var userPosition = new UserPosition();
        userPosition.setId(UserPositionId.of(user, position));
        userPosition.setUser(user);
        userPosition.setPosition(position);
        return userPosition;
    }

    @EmbeddedId
    private UserPositionId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Position position;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
