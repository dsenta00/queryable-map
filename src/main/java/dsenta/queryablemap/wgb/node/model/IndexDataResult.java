package dsenta.queryablemap.wgb.node.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IndexDataResult<K extends Comparable<K>, T> {
    Integer index;
    WgbData<K, T> data;

    public static <K extends Comparable<K>, T>
    int compare(IndexDataResult<K, T> x, IndexDataResult<K, T> y) {
        return x.getData().getKey().compareTo(y.getData().getKey());
    }
}