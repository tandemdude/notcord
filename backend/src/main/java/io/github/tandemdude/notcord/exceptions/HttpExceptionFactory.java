package io.github.tandemdude.notcord.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HttpExceptionFactory {
    public static ResponseStatusException tokenFormatInvalidException() {
        return new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Token format is invalid - must match: '<type> <token>'"
        );
    }

    public static ResponseStatusException invalidTokenException() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The supplied token is not recognised");
    }

    public static ResponseStatusException missingRequiredPermissionsException() {
        return new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You do not have permission to access the requested resource"
        );
    }

    public static ResponseStatusException resourceNotFoundException(String details) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, details);
    }
}
