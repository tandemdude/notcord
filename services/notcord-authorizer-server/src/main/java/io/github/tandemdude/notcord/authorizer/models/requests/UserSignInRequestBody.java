package io.github.tandemdude.notcord.authorizer.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSignInRequestBody {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
