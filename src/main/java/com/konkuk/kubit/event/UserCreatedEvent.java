package com.konkuk.kubit.event;

import com.konkuk.kubit.domain.User;

public class UserCreatedEvent {
    private User user;

    public UserCreatedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}