package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WhiteNode<K extends Comparable<K>, T> extends NonGreyNode<K, T> implements Serializable {
    private static final long serialVersionUID = 6099943686787969559L;

    public WhiteNode(WgbData<K, T> data, int capacity) {
        super(data, capacity);
    }
}