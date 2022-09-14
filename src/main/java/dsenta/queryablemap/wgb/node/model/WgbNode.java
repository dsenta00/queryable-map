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
package dsenta.queryablemap.wgb.node.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class WgbNode<K extends Comparable<K>, T> implements Serializable {
    private static final long serialVersionUID = 3740285769178156368L;
    private int depth;
    private int capacity;
    private WgbData<K, T> data;
    private WgbNode<K, T>[] nodes;

    public WgbNode(WgbData<K, T> data, int capacity) {
        this.data = data;
        this.depth = 1;
        this.capacity = capacity;
    }

    public abstract int nextIndex(WgbKey<K> key);

    public WgbKey<K> getKey() {
        return data.getKey();
    }

    public WgbNode<K, T> get(int index) {
        return Objects.isNull(nodes) ? null : nodes[index];
    }

    @SuppressWarnings("unchecked")
    public void setNode(int index, WgbNode<K, T> node) {
        nodes = Objects.isNull(nodes) ? new WgbNode[capacity] : nodes;
        nodes[index] = node;
    }

    public void setKey(WgbKey<K> key) {
        this.data.setKey(key);
    }

    public List<WgbNode<K, T>> getNodes() {
        return Optional.ofNullable(this.nodes)
                .map(Arrays::asList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int getNumberOfEmptySlots() {
        return Objects.isNull(this.nodes) ? 0 : (int) Stream.of(this.nodes).filter(Objects::isNull).count();
    }

    public Entry<K, T> toMapEntry() {
        return Map.entry(data.getKey().getValue(), data.getValue());
    }
}