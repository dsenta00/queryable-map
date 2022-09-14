package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;

public class WgbKey<K extends Comparable<K>> implements Comparable<WgbKey<K>>, Serializable {
    private static final long serialVersionUID = -4506283214990878590L;
    private int hash;
    private K value;

    public WgbKey(K value) {
        this.setValue(value);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public K getValue() {
        return value;
    }

    public void setValue(K value) {
        this.value = value;
        this.hash = value.hashCode();
    }

    @Override
    public int compareTo(WgbKey<K> o) {
        return this.getValue().compareTo(o.getValue());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}