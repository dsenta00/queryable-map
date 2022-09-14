package dsenta.queryablemap.wgb.node.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GreyNodeWithIndex<K extends Comparable<K>, T> {
    Integer index;
    GreyNode<K, T> greyNode;
}