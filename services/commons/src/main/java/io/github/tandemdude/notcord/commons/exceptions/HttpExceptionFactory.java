/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tandemdude.notcord.commons.exceptions;

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

    public static ResponseStatusException badRequestException(String details) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, details);
    }

    public static ResponseStatusException conflictException(String details) {
        return new ResponseStatusException(HttpStatus.CONFLICT, details);
    }
}
