package com.ua.teamconnect.tracker.model.entity.id;

import com.ua.teamconnect.tracker.model.entity.Stack;
import com.ua.teamconnect.tracker.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserStackId implements Serializable {

    public static UserStackId of(User user, Stack stack) {
        var id = new UserStackId();
        id.setUserId(user.getId());
        id.setStackId(stack.getId());
        return id;
    }

    @Column(nullable = false, name = "user_id")
    private Integer userId;

    @Column(nullable = false, name = "stack_id")
    private Integer stackId;
}
