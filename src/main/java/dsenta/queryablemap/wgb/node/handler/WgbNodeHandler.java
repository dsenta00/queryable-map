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
package dsenta.queryablemap.wgb.node.handler;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dsenta.queryablemap.wgb.node.model.GreyNode;
import dsenta.queryablemap.wgb.node.model.GreyNodeWithIndex;
import dsenta.queryablemap.wgb.node.model.IndexDataResult;
import dsenta.queryablemap.wgb.node.model.NonGreyNode;
import dsenta.queryablemap.wgb.node.model.WgbKey;
import dsenta.queryablemap.wgb.node.model.WgbNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WgbNodeHandler {

    public static <K extends Comparable<K>, T>
    int depth(WgbNode<K, T> wgbNode) {
        if (isNull(wgbNode)) {
            return 0;
        }

        return wgbNode.getDepth();
    }

    public static <K extends Comparable<K>, T>
    int getNumberOfNodes(WgbNode<K, T> wgbNode) {
        if (isNull(wgbNode)) {
            return 0;
        }

        return wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::getNumberOfNodes)
                .reduce(Integer::sum)
                .orElse(0) + 1;
    }

    public static <K extends Comparable<K>, T>
    int getNumberOfEmptyNodes(WgbNode<K, T> wgbNode) {
        if (isNull(wgbNode)) {
            return 0;
        }

        return wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::getNumberOfEmptyNodes)
                .reduce(Integer::sum)
                .orElse(0) + wgbNode.getNumberOfEmptySlots();
    }

    public static <K extends Comparable<K>, T>
    boolean containsValue(WgbNode<K, T> wgbNode, T data) {
        if (isNull(wgbNode)) {
            return false;
        }

        if (wgbNode.getData().getValue().equals(data)) {
            return true;
        }

        return wgbNode.getNodes().stream()
                .anyMatch(childNode -> containsValue(childNode, data));
    }

    public static <K extends Comparable<K>, T>
    Entry<K, T> get(WgbNode<K, T> wgbNode, WgbKey<K> key) {
        if (isNull(wgbNode)) {
            return null;
        }

        do {
            if (wgbNode.getKey().compareTo(key) == 0) {
                return wgbNode.toMapEntry();
            }

            wgbNode = wgbNode.get(wgbNode.nextIndex(key));
        } while (nonNull(wgbNode));

        return null;
    }

    public static <K extends Comparable<K>, T>
    Set<K> keySet(WgbNode<K, T> wgbNode) {
        if (isNull(wgbNode)) {
            return new HashSet<>();
        }

        Set<K> result = wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        result.add(wgbNode.getKey().getValue());

        return result;
    }

    public static <K extends Comparable<K>, T>
    Set<Entry<K, T>> entrySet(WgbNode<K, T> wgbNode) {
        if (isNull(wgbNode)) {
            return new HashSet<>();
        }

        Set<Entry<K, T>> result = wgbNode.getNodes()
                .stream()
                .map(WgbNodeHandler::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        result.add(Map.entry(wgbNode.getKey().getValue(), wgbNode.getData().getValue()));

        return result;
    }

    public static <K extends Comparable<K>, T>
    void calculateAndSetDepth(WgbNode<K, T> wgbNode) {
        wgbNode.setDepth(
                wgbNode
                        .getNodes()
                        .stream()
                        .map(WgbNodeHandler::depth)
                        .max(Integer::compareTo)
                        .orElse(0) + 1
        );
    }

    public static <K extends Comparable<K>, T>
    IndexDataResult<K, T> getChildNodeWithMinData(NonGreyNode<K, T> nonGreyNode) {
        return IntStream.range(0, nonGreyNode.getCapacity())
                .boxed()
                .map(i -> new GreyNodeWithIndex<>(i, nonGreyNode.get(i)))
                .filter(o -> nonNull(o.getGreyNode()))
                .map(o -> new IndexDataResult<>(o.getIndex(), GreyNodeHandler.getMin(o.getGreyNode())))
                .filter(o -> nonNull(o.getData()))
                .min(IndexDataResult::compare)
                .orElse(new IndexDataResult<>(null, null));
    }

    public static <K extends Comparable<K>, T>
    IndexDataResult<K, T> getChildNodeWithMaxData(NonGreyNode<K, T> nonGreyNode) {
        return IntStream.range(0, nonGreyNode.getCapacity())
                .boxed()
                .map(i -> new GreyNodeWithIndex<>(i, nonGreyNode.get(i)))
                .filter(o -> nonNull(o.getGreyNode()))
                .map(o -> new IndexDataResult<>(o.getIndex(), GreyNodeHandler.getMax(o.getGreyNode())))
                .filter(o -> nonNull(o.getData()))
                .max(IndexDataResult::compare)
                .orElse(new IndexDataResult<>(null, null));
    }

    public static <K extends Comparable<K>, T>
    List<List<Entry<K, T>>> collectFromGreyNodes(NonGreyNode<K, T> nonGreyNode, Function<GreyNode<K, T>, List<Entry<K, T>>> mapper) {
        return nonGreyNode.getNodes().stream()
                .map(node -> (GreyNode<K, T>) node)
                .map(mapper)
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());
    }

    public static <K extends Comparable<K>, T>
    void printDepth(NonGreyNode<K, T> nonGreyNode, int depth) {
        if (isNull(nonGreyNode)) {
            System.out.println("null");
            return;
        }

        System.out.println(nonGreyNode.getKey());

        for (int i = 0; i < nonGreyNode.getCapacity(); i++) {
            if (Objects.nonNull(nonGreyNode.get(i))) {
                for (int j = 0; j < depth + 2; j++) {
                    System.out.print(" ");
                }

                System.out.print(i + "%" + nonGreyNode.getCapacity() + ": ");
                GreyNodeHandler.printDepth(nonGreyNode.get(i), depth + 4);
            }
        }
    }
}