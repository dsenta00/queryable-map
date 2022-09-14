package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BlackNode<K extends Comparable<K>, T> extends NonGreyNode<K, T> implements Serializable {
    private static final long serialVersionUID = -6079875398947273613L;

    public BlackNode(WgbData<K, T> data, int capacity) {
        super(data, capacity);
    }
}