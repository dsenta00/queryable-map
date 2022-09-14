package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GreyNode<K extends Comparable<K>, T> extends WgbNode<K, T> implements Serializable {
    private static final long serialVersionUID = 1083948174594508322L;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOTAL = 2;

    public GreyNode(WgbData<K, T> data) {
        super(data, TOTAL);
    }

    @Override
    public int nextIndex(WgbKey<K> key) {
        return key.compareTo(this.getKey()) > 0 ? RIGHT : LEFT;
    }

    public WhiteNode<K, T> getWhiteNode() {
        return (WhiteNode<K, T>) this.get(LEFT);
    }

    public BlackNode<K, T> getBlackNode() {
        return (BlackNode<K, T>) this.get(RIGHT);
    }

    public void setWhiteNode(WhiteNode<K, T> node) {
        this.setNode(LEFT, node);
    }

    public void setBlackNode(BlackNode<K, T> node) {
        this.setNode(RIGHT, node);
    }
}
