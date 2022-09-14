package dsenta.queryablemap.trie.node.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pair<T1, T2> {
    T1 first;
    T2 second;

    public Pair<String, String> asStringPair() {
        return toStringPair(this);
    }

    public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
        return Pair.<T1, T2>builder()
                .first(first)
                .second(second)
                .build();
    }

    public static <T1, T2> Pair<String, String> toStringPair(Pair<T1, T2> pair) {
        return Pair.<String, String>builder()
                .first(String.valueOf(pair.getFirst()))
                .second(String.valueOf(pair.getSecond()))
                .build();
    }
}