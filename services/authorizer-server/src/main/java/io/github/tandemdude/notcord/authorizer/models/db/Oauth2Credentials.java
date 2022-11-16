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

package io.github.tandemdude.notcord.authorizer.models.db;

import io.github.tandemdude.notcord.commons.utility.DefaultAvatarGenerator;
import io.github.tandemdude.notcord.commons.utility.SnowflakeGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "oauth2_credentials", schema = "notcord")
public class Oauth2Credentials implements Persistable<String> {
    @Id
    private String clientId = null;
    private String clientSecret = null;
    private String appName;
    private String redirectUri;
    private String ownerId;
    private String defaultIconSvg = null;

    public Oauth2Credentials(String appName, String redirectUri, String ownerId) {
        this.appName = appName;
        this.redirectUri = redirectUri;
        this.ownerId = ownerId;
    }

    @Override
    public String getId() {
        return this.clientId;
    }

    @Override
    public boolean isNew() {
        var isNew = this.clientId == null;
        if (isNew) {
            this.clientId = SnowflakeGenerator.newSnowflake();
            this.clientSecret = UUID.randomUUID().toString().replace("-", "");
            this.defaultIconSvg = DefaultAvatarGenerator.generateDefaultAppIconSvg(this.appName);
        }
        return isNew;
    }
}
