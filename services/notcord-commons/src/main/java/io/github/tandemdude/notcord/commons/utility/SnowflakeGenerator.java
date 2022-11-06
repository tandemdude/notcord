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

package io.github.tandemdude.notcord.commons.utility;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class SnowflakeGenerator {
    private static final long EPOCH = 1638230400000L;
    private static final BigInteger snowflakeBase;
    private static final AtomicInteger generatedIds = new AtomicInteger();

    static {
        snowflakeBase = BigInteger
            .valueOf(Long.parseLong(System.getenv("NC_WORKER_ID")) << 17)
            .add(BigInteger.valueOf(Long.parseLong(System.getenv("NC_PROCESS_ID")) << 12));
    }

    public static String newSnowflake() {
        return snowflakeBase
            .add(BigInteger.valueOf(generatedIds.getAndAccumulate(1, (x, y) -> (x + y) % 4096)))
            .add(BigInteger.valueOf(System.currentTimeMillis() - EPOCH).shiftLeft(22))
            .toString();
    }
}
