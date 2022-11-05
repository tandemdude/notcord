package io.github.tandemdude.notcord.rest.models.responses;

import io.github.tandemdude.notcord.commons.entities.User;
import lombok.Data;

@Data
public class UserResponse {
    private final String id;
    private final String username;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
