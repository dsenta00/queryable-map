package dsenta.queryablemap.trie.node.model;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrieNode<K extends Comparable<K>, T> implements Entry<K, T>, Serializable {
    private static final long serialVersionUID = -1371258515026817545L;
    private K key;
    private T value;
    private final Map<Character, TrieNode<K, T>> next = new HashMap<>();

    public static <K extends Comparable<K>, T>
    TrieNode<K, T> of(K key, T value) {
        return new TrieNode<>(key, value);
    }

    public static <K extends Comparable<K>, T>
    TrieNode<K, T> empty() {
        return new TrieNode<>(null, null);
    }

    @Override
    public T setValue(T value) {
        T oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    public Entry<K, T> copy() {
        return TrieNode.of(key, value);
    }

    public boolean hasNext() {
        return !next.isEmpty();
    }

    public boolean isEmpty() {
        return isNull(value) && next.isEmpty();
    }
}