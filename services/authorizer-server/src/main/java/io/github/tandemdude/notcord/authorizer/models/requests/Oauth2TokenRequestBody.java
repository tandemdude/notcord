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

package io.github.tandemdude.notcord.authorizer.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Oauth2TokenRequestBody {
    // snake_case names necessary by OAuth spec [RFC-6749]
    @NotNull
    private String grant_type;
    private String code;
    private String redirect_uri;
    @NotNull
    private String client_id;
    @NotNull
    private String client_secret;
    private String refresh_token;
}
