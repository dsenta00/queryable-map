package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class WgbData<K extends Comparable<K>, T> implements Map.Entry<WgbKey<K>, T>, Serializable {
    private static final long serialVersionUID = -7572084372968472706L;
    private WgbKey<K> key;
    private T value;

    public WgbData(K key, T value) {
        this.key = new WgbKey<>(key);
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        var other = (WgbData<K, T>) obj;
        return this.getKey().compareTo(other.getKey()) == 0;
    }

    @Override
    public T setValue(T value) {
        T oldValue = this.value;
        this.value = value;
        return oldValue;
    }
}