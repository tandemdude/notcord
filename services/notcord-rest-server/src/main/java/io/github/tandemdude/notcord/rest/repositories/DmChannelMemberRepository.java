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

package io.github.tandemdude.notcord.rest.repositories;

import io.github.tandemdude.notcord.rest.models.db.DmChannelMember;
import io.github.tandemdude.notcord.rest.models.db.enums.ChannelType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DmChannelMemberRepository extends ReactiveCrudRepository<DmChannelMember, String> {
    Flux<DmChannelMember> findAllByUserIdAndChannelType(String userId, ChannelType channelType);

    Flux<DmChannelMember> findAllByChannelId(String channelId);

    Mono<Boolean> existsByChannelIdAndUserId(String channelId, String userId);
}
