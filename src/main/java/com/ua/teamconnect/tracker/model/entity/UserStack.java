package com.ua.teamconnect.tracker.model.entity;

import com.ua.teamconnect.tracker.model.entity.id.UserStackId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_stack")
@Getter @Setter
public class UserStack {

    public static UserStack of(User user, Stack stack) {
        var userStack = new UserStack();
        userStack.setId(UserStackId.of(user, stack));
        userStack.setUser(user);
        userStack.setStack(stack);
        return userStack;
    }

    @EmbeddedId
    private UserStackId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Stack stack;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false, name = "isprimary")
    private Boolean isPrimary;
}
