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

package io.github.tandemdude.notcord.commons.enums;

public class EventType {
    public static final long GUILD_CREATE = 1;
    public static final long GUILD_DELETE = 1 << 1;
    public static final long MESSAGE_CREATE = 1 << 2;
    public static final long MESSAGE_UPDATE = 1 << 3;
    public static final long MESSAGE_DELETE = 1 << 4;
    public static final long CHANNEL_CREATE = 1 << 5;
    public static final long CHANNEL_UPDATE = 1 << 6;
    public static final long CHANNEL_DELETE = 1 << 7;

    public static boolean contains(long bitfield, long value) {
        return (bitfield & value) == value;
    }
}
