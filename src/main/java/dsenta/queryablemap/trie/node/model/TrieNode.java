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