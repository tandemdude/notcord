package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserCreateRequestBody {
    private @NotNull String username;
    private @NotNull String email;
    private @NotNull String password;
}
