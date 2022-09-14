/**
 * MIT License
 *
 * Copyright (c) 2022 Duje Senta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dsenta.queryablemap.trie;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dsenta.queryablemap.QueryableMap;
import dsenta.queryablemap.trie.node.handler.TrieNodeHandler;
import dsenta.queryablemap.trie.node.model.TrieNode;

public class Trie<K extends Comparable<K>, T> implements QueryableMap<K, T>, Serializable {
    private static final long serialVersionUID = 6308320094740168730L;
    private final TrieNode<K, T> root = TrieNode.empty();
    private int count;

    @Override
    public int getNumberOfNodes() {
        return count + 1;
    }

    @Override
    public int getNumberOfEmptyNodes() {
        return count + 1;
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public String getName() {
        return "trie";
    }

    @Override
    public Entry<K, T> getByKey(K key) {
        var value = TrieNodeHandler.getByKey(root, key);
        return isNull(value) ? null : Map.entry(key, value);
    }

    @Override
    public List<Entry<K, T>> getBiggerThanAsc(K key) {
        return TrieNodeHandler.getBiggerThan(root, key, true);
    }

    @Override
    public List<Entry<K, T>> getBiggerThanDesc(K key) {
        return TrieNodeHandler.getBiggerThan(root, key, false);
    }

    @Override
    public List<Entry<K, T>> getLessThanAsc(K key) {
        return TrieNodeHandler.getLessThan(root, key, true);
    }

    @Override
    public List<Entry<K, T>> getLessThanDesc(K key) {
        return TrieNodeHandler.getLessThan(root, key, false);
    }

    @Override
    public List<Entry<K, T>> getBiggerThanEqualsAsc(K key) {
        return TrieNodeHandler.getBiggerThanEquals(root, key, true);
    }

    @Override
    public List<Entry<K, T>> getBiggerThanEqualsDesc(K key) {
        return TrieNodeHandler.getBiggerThanEquals(root, key, false);
    }

    @Override
    public List<Entry<K, T>> getLessThanEqualsAsc(K key) {
        return TrieNodeHandler.getLessThanEquals(root, key, true);
    }

    @Override
    public List<Entry<K, T>> getLessThanEqualsDesc(K key) {
        return TrieNodeHandler.getLessThanEquals(root, key, false);
    }

    @Override
    public List<Entry<K, T>> getBetweenAsc(K low, K high) {
        return TrieNodeHandler.getBetween(root, low, high, true);
    }

    @Override
    public List<Entry<K, T>> getBetweenDesc(K low, K high) {
        return TrieNodeHandler.getBetween(root, low, high, false);
    }

    @Override
    public List<Entry<K, T>> getNotEqualsAsc(K key) {
        return TrieNodeHandler.getNotEquals(root, key, true);
    }

    @Override
    public List<Entry<K, T>> getNotEqualsDesc(K key) {
        return TrieNodeHandler.getNotEquals(root, key, false);
    }

    @Override
    public List<Entry<K, T>> getAsc() {
        return TrieNodeHandler.get(root, true);
    }

    @Override
    public List<Entry<K, T>> getDesc() {
        return TrieNodeHandler.get(root, false);
    }

    @Override
    public K getMin() {
        return TrieNodeHandler.getMin(root);
    }

    @Override
    public K getMax() {
        return TrieNodeHandler.getMax(root);
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public boolean containsKey(Object key) {
        return nonNull(TrieNodeHandler.getByKey(root, (K) key));
    }

    @Override
    public boolean containsValue(Object value) {
        throw new RuntimeException();
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public T get(Object key) {
        return TrieNodeHandler.getByKey(root, (K) key);
    }

    @Override
    public synchronized T put(K key, T value) {
        T oldValue = TrieNodeHandler.insert(root, key, value);

        if (isNull(oldValue)) {
            count++;
        }

        return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public T remove(Object key) {
        T value = TrieNodeHandler.delete(root, (K) key);

        if (nonNull(value)) {
            count--;
        }

        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        this.root.getNext().clear();
        this.root.setKey(null);
        this.root.setValue(null);
        this.count = 0;
    }

    @Override
    public Set<K> keySet() {
        return TrieNodeHandler.keySet(root);
    }

    @Override
    public Collection<T> values() {
        return TrieNodeHandler.values(root);
    }

    @Override
    public Set<Entry<K, T>> entrySet() {
        return TrieNodeHandler.entrySet(root);
    }
}