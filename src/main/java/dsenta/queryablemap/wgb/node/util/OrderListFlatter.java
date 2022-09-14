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
package dsenta.queryablemap.wgb.node.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import dsenta.queryablemap.exception.ShouldNeverHappenException;

public class OrderListFlatter {

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> flatAsc(List<List<Entry<K, T>>> inOrderLists) {
        List<Entry<K, T>> result = new LinkedList<>();

        inOrderLists = inOrderLists
                .stream()
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        while (!inOrderLists.isEmpty()) {
            List<Entry<K, T>> minList = inOrderLists
                    .stream()
                    .min(Comparator.comparing(list -> list.get(0).getKey()))
                    .orElseThrow(ShouldNeverHappenException::new);

            result.add(minList.get(0));
            minList.remove(0);

            inOrderLists = inOrderLists
                    .stream()
                    .filter(list -> !list.isEmpty())
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> flatDesc(List<List<Entry<K, T>>> inOrderLists) {
        List<Entry<K, T>> result = new LinkedList<>();

        inOrderLists = inOrderLists
                .stream()
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        while (!inOrderLists.isEmpty()) {
            List<Entry<K, T>> maxList = inOrderLists
                    .stream()
                    .max(Comparator.comparing(list -> list.get(0).getKey()))
                    .orElseThrow(ShouldNeverHappenException::new);

            result.add(maxList.get(0));
            maxList.remove(0);

            inOrderLists = inOrderLists
                    .stream()
                    .filter(list -> !list.isEmpty())
                    .collect(Collectors.toList());
        }

        return result;
    }
}