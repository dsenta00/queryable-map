package dsenta.queryablemap.statistics;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StopWatchExecutor {

    public static double measureTime(Runnable runnable) {
        Instant startTime = Instant.now();
        runnable.run();
        Instant endTime = Instant.now();
        long milliseconds = endTime.toEpochMilli() - startTime.toEpochMilli();
        return (double) milliseconds / 1000;
    }
}
