package io.github.tandemdude.notcord.utils;

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
