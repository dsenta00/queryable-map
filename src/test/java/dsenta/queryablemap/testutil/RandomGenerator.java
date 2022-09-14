package dsenta.queryablemap.testutil;

import static dsenta.queryablemap.testutil.StringHexByteMapper.bytesToHex;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.stream.Stream;

public final class RandomGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    private RandomGenerator() {
        secureRandom.setSeed(Instant.now().toEpochMilli());
    }

    public static Stream<Integer> streamRandomNumbers(int maxAmountOfData) {
        return secureRandom.ints(maxAmountOfData)
                .parallel()
                .boxed()
                .map(Math::abs)
                .map(i -> i % maxAmountOfData)
                .distinct();
    }

    public static String generate256HexString() {
        return bytesToHex(secureRandom.generateSeed(32));
    }
}