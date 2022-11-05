package io.github.tandemdude.notcord.authorizer.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateRequestBody {
    @NotNull
    @Pattern(regexp = "^[\\w\\-.]{5,40}$")
    private String username;
    @Email
    @NotNull
    private String email;
    @NotNull
    private String password;
}
