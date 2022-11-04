package io.github.tandemdude.notcord.authorizer.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSignInRequestBody {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
