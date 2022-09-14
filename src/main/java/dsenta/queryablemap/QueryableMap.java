package dsenta.queryablemap;

import java.util.List;
import java.util.Map;

public interface QueryableMap<K extends Comparable<K>, T> extends Map<K, T> {
    int getNumberOfNodes();
    int getNumberOfEmptyNodes();
    int depth();
    String getName();
    Entry<K, T> getByKey(K key);
    List<Entry<K, T>> getBiggerThanAsc(K key);
    List<Entry<K, T>> getBiggerThanDesc(K key);
    List<Entry<K, T>> getLessThanAsc(K key);
    List<Entry<K, T>> getLessThanDesc(K key);
    List<Entry<K, T>> getBiggerThanEqualsAsc(K key);
    List<Entry<K, T>> getBiggerThanEqualsDesc(K key);
    List<Entry<K, T>> getLessThanEqualsAsc(K key);
    List<Entry<K, T>> getLessThanEqualsDesc(K key);
    List<Entry<K, T>> getBetweenAsc(K low, K max);
    List<Entry<K, T>> getBetweenDesc(K low, K max);
    List<Entry<K, T>> getNotEqualsAsc(K key);
    List<Entry<K, T>> getNotEqualsDesc(K key);
    List<Entry<K, T>> getAsc();
    List<Entry<K, T>> getDesc();
    K getMin();
    K getMax();
}