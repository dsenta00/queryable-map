package dsenta.queryablemap.wgb;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import dsenta.queryablemap.QueryableMap;
import dsenta.queryablemap.exception.NotFoundException;
import dsenta.queryablemap.exception.UniqueException;
import dsenta.queryablemap.wgb.node.handler.GreyNodeHandler;
import dsenta.queryablemap.wgb.node.handler.WgbNodeHandler;
import dsenta.queryablemap.wgb.node.model.GreyNode;
import dsenta.queryablemap.wgb.node.model.WgbData;
import dsenta.queryablemap.wgb.node.model.WgbKey;
import dsenta.queryablemap.wgb.node.util.PrimeConstants;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WhiteGreyBlackTree<K extends Comparable<K>, T> implements QueryableMap<K, T>, Serializable {
    private static final long serialVersionUID = 7553380290813734182L;
    private int count;
    private GreyNode<K, T> greyNode;
    private int firstPrime = PrimeConstants.FIRST_PRIME;

    public WhiteGreyBlackTree(int firstPrime) {
        this.firstPrime = firstPrime;
    }

    public synchronized void print() {
        GreyNodeHandler.printDepth(greyNode, 4);
    }

    public synchronized Entry<K, T> getByKey(K key) {
        return WgbNodeHandler.get(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getBiggerThanAsc(K key) {
        return GreyNodeHandler.getBiggerThanAsc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getBiggerThanDesc(K key) {
        return GreyNodeHandler.getBiggerThanDesc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getLessThanAsc(K key) {
        return GreyNodeHandler.getLessThanAsc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getLessThanDesc(K key) {
        return GreyNodeHandler.getLessThanDesc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getBiggerThanEqualsAsc(K key) {
        return GreyNodeHandler.getBiggerThanEqualsAsc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getBiggerThanEqualsDesc(K key) {
        return GreyNodeHandler.getBiggerThanEqualsDesc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getLessThanEqualsAsc(K key) {
        return GreyNodeHandler.getLessThanEqualsAsc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getLessThanEqualsDesc(K key) {
        return GreyNodeHandler.getLessThanEqualsDesc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getBetweenAsc(K low, K max) {
        return GreyNodeHandler.getBetweenAsc(greyNode, new WgbKey<>(low), new WgbKey<>(max));
    }

    public synchronized List<Entry<K, T>> getBetweenDesc(K low, K max) {
        return GreyNodeHandler.getBetweenDesc(greyNode, new WgbKey<>(low), new WgbKey<>(max));
    }

    public synchronized List<Entry<K, T>> getNotEqualsAsc(K key) {
        return GreyNodeHandler.getNotEqualsAsc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getNotEqualsDesc(K key) {
        return GreyNodeHandler.getNotEqualsDesc(greyNode, new WgbKey<>(key));
    }

    public synchronized List<Entry<K, T>> getAsc() {
        return GreyNodeHandler.getAsc(greyNode);
    }

    public synchronized List<Entry<K, T>> getDesc() {
        return GreyNodeHandler.getDesc(greyNode);
    }

    @Override
    public synchronized int getNumberOfNodes() {
        return WgbNodeHandler.getNumberOfNodes(greyNode);
    }

    @Override
    public synchronized int getNumberOfEmptyNodes() {
        return WgbNodeHandler.getNumberOfEmptyNodes(greyNode);
    }

    @Override
    public synchronized int depth() {
        return WgbNodeHandler.depth(greyNode);
    }

    @Override
    public String getName() {
        return "white_grey_black";
    }

    @Override
    public synchronized int size() {
        return this.count;
    }

    @Override
    public synchronized boolean isEmpty() {
        return isNull(greyNode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean containsKey(Object key) {
        return Objects.nonNull(WgbNodeHandler.get(greyNode, new WgbKey<>((K) key)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean containsValue(Object value) {
        return WgbNodeHandler.containsValue(greyNode, (T) value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized T get(Object key) {
        Entry<K, T> result = WgbNodeHandler.get(greyNode, new WgbKey<>((K) key));

        return isNull(result) ? null : result.getValue();
    }

    @Override
    public synchronized T put(K key, T value) {
        WgbData<K, T> data = new WgbData<>(key, value);

        try {
            greyNode = GreyNodeHandler.insert(greyNode, firstPrime, data);
            this.count++;
            return value;
        } catch (UniqueException e) {
            Entry<K, T> oldData = WgbNodeHandler.get(greyNode, data.getKey());

            try {
                greyNode = GreyNodeHandler.delete(greyNode, data.getKey());
                greyNode = GreyNodeHandler.insert(greyNode, firstPrime, data);
            } catch (NotFoundException | UniqueException ignored) {
            }

            assert oldData != null;
            return oldData.getValue();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized T remove(Object key) {
        WgbKey<K> wgbKey = new WgbKey<>((K) key);
        Entry<K, T> oldData = WgbNodeHandler.get(greyNode, wgbKey);

        if (isNull(oldData)) {
            return null;
        }

        try {
            greyNode = GreyNodeHandler.delete(greyNode, wgbKey);
            this.count--;
        } catch (NotFoundException | UniqueException ignored) {
        }

        return oldData.getValue();
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public synchronized void clear() {
        this.greyNode = null;
        this.count = 0;
    }

    @NotNull
    @Override
    public synchronized Set<K> keySet() {
        return WgbNodeHandler.keySet(greyNode);
    }

    @NotNull
    @Override
    public synchronized Collection<T> values() {
        return GreyNodeHandler.getAsc(greyNode)
                .stream()
                .map(Entry::getValue)
                .collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public synchronized Set<Entry<K, T>> entrySet() {
        return WgbNodeHandler.entrySet(greyNode);
    }

    @Override
    public synchronized K getMax() {
        WgbData<K, T> data = GreyNodeHandler.getMax(greyNode);

        return isNull(data) ? null : data.getKey().getValue();
    }

    @Override
    public synchronized K getMin() {
        WgbData<K, T> data = GreyNodeHandler.getMin(greyNode);

        return isNull(data) ? null : data.getKey().getValue();
    }
}