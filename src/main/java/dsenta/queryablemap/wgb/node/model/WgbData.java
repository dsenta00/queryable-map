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