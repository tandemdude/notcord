package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

@Data
public class UserCreateRequestBody {
    private String username;
    private String email;
    private String password;
}
