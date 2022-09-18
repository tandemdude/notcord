package io.github.tandemdude.notcord.models.responses;

import io.github.tandemdude.notcord.models.db.User;
import lombok.Data;

@Data
public class UserResponse {
    private final String id;
    private final String username;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId().toString(), user.getUsername());
    }
}
