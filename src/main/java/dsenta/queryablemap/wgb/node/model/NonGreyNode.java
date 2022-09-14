package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;

import dsenta.queryablemap.wgb.node.util.Mod;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NonGreyNode<K extends Comparable<K>, T> extends WgbNode<K, T> implements Serializable {
    private static final long serialVersionUID = -6079875398947273613L;

    public NonGreyNode(WgbData<K, T> data, int capacity) {
        super(data, capacity);
    }

    @Override
    public GreyNode<K, T> get(int index) {
        return (GreyNode<K, T>) super.get(index);
    }

    public GreyNode<K, T> next(WgbKey<K> key) {
        return this.get(nextIndex(key));
    }

    @Override
    public int nextIndex(WgbKey<K> key) {
        return Mod.fastMod(Math.abs(key.hashCode()), this.getCapacity());
    }
}