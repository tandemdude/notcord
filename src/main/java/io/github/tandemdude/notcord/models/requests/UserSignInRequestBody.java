package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

@Data
public class UserSignInRequestBody {
    private String username;
    private String password;
}
