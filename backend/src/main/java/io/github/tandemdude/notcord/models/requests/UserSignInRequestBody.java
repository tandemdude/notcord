package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserSignInRequestBody {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
