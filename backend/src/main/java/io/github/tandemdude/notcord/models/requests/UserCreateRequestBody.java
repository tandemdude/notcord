package io.github.tandemdude.notcord.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateRequestBody {
    private @NotNull String username;
    private @NotNull String email;
    private @NotNull String password;
}
