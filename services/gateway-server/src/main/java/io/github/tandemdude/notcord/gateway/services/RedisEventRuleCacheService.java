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

package io.github.tandemdude.notcord.gateway.services;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisEventRuleCacheService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public RedisEventRuleCacheService(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> save(String userId, String hashKey, long rule) {
        return reactiveRedisTemplate
            .opsForValue()
            .set("{" + userId + "}" + hashKey, String.valueOf(rule));
    }

    public Mono<Long> get(String userId, String hashKey) {
        return reactiveRedisTemplate
            .opsForValue()
            .get("{" + userId + "}" + hashKey)
            .map(Long::parseLong);
    }
}
