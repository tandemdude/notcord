package io.github.tandemdude.notcord.models.oauth2;

import lombok.Getter;

import java.util.*;

public class Scope {
    // Private scopes
    public static final long USER = 1;
    public static final long BOT = 1 << 1;

    // Public scopes
    public static final long IDENTITY_READ = 1 << 2;
    public static final long GUILDS_READ = 1 << 3;
    public static final long GUILDS_JOIN = 1 << 4;
    public static final long GUILDS_MEMBER_READ = 1 << 5;

    @Getter
    private static final Map<Long, String> scopeDescriptionMap = Map.of(
        USER, "???", BOT, "???", IDENTITY_READ, "Read your username and avatar", GUILDS_READ, "See which guilds you are in",
        GUILDS_JOIN, "Join guilds on your behalf", GUILDS_MEMBER_READ, "Read your nickname, roles, and permissions in the guilds you are in"
    );

    @Getter
    private static final Map<Long, String> scopeNameMap = Map.of(
        USER, "user", BOT, "bot", IDENTITY_READ, "identity.read",
        GUILDS_READ, "guilds.read", GUILDS_JOIN, "guilds.join", GUILDS_MEMBER_READ, "guilds.member.read"
    );
    @Getter
    private static final Map<String, Long> scopeValueMap = new HashMap<>();

    static {
        scopeNameMap.forEach((k, v) -> scopeValueMap.put(v, k));
    }

    public static long bitfieldFromScopes(List<String> scopes) {
        return scopes.stream()
            .mapToLong(scopeValueMap::get)
            .filter(Objects::nonNull)
            .sum();
    }

    public static List<String> scopesFromBitfield(long bitfield) {
        return scopeValueMap.entrySet().stream()
            .map(entry -> (bitfield & entry.getValue()) == bitfield ? entry.getKey() : null)
            .filter(Objects::nonNull)
            .toList();
    }

    public static boolean grantsAll(long bitfield, long... scopes) {
        return Arrays.stream(scopes).allMatch(value -> (value & bitfield) == value);
    }

    public static boolean grantsAny(long bitfield, long... scopes) {
        return Arrays.stream(scopes).anyMatch(value -> (value & bitfield) == value);
    }
}
