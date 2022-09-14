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