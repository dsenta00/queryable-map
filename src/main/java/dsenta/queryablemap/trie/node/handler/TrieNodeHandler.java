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
package dsenta.queryablemap.trie.node.handler;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import dsenta.queryablemap.trie.node.model.Pair;
import dsenta.queryablemap.trie.node.model.TrieNode;
import dsenta.queryablemap.wgb.node.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrieNodeHandler {

    public static <K extends Comparable<K>, T>
    T insert(TrieNode<K, T> root, K key, T value) {
        if (isNull(key)) {
            T oldValue = root.getValue();
            root.setValue(value);
            return oldValue;
        }

        var keyStr = String.valueOf(key);
        var current = root;

        if (!StringUtils.isEmpty(keyStr)) {
            for (Character c : keyStr.toCharArray()) {
                var next = current.getNext().getOrDefault(c, null);

                if (isNull(next)) {
                    next = TrieNode.empty();
                    current.getNext().put(c, next);
                }

                current = next;
            }
        }

        current.setKey(key);

        return current.setValue(value);
    }

    public static <K extends Comparable<K>, T>
    T getByKey(TrieNode<K, T> root, K key) {
        if (isNull(root)) {
            return null;
        }

        if (isNull(key)) {
            return root.getValue();
        }

        var keyStr = String.valueOf(key);

        if (StringUtils.isEmpty(keyStr)) {
            return root.getValue();
        }

        var current = root;

        for (char c : keyStr.toCharArray()) {
            if (isNull(current)) {
                return null;
            } else {
                current = current.getNext().getOrDefault(c, null);
            }
        }

        if (isNull(current)) {
            return null;
        }

        return current.getValue();
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> get(TrieNode<K, T> root, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        var entries = new LinkedList<Entry<K, T>>();
        var nodes = new LinkedList<Pair<Boolean, TrieNode<K, T>>>();
        nodes.addFirst(Pair.of(false, root));

        do {
            nodes.forEach(pair -> pair.setFirst(true)); // Mark them that they are about to be processed

            do {
                var pair = nodes.removeFirst();

                if (pair.getFirst()) {
                    var node = pair.getSecond();

                    if (nonNull(node.getValue())) {
                        entries.add(node.copy());
                    }

                    node.getNext().entrySet().stream()
                            .sorted((e1, e2) -> compareEntries(e1, e2, asc))
                            .forEach(e -> nodes.addFirst(Pair.of(false, e.getValue())));
                } else {
                    nodes.addFirst(pair);
                    break;
                }
            } while (!nodes.isEmpty());

        } while (!nodes.isEmpty());

        return entries;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getNotEquals(TrieNode<K, T> root, K key, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        var entries = new LinkedList<Entry<K, T>>();
        var nodes = new LinkedList<Pair<Boolean, TrieNode<K, T>>>();
        nodes.addFirst(Pair.of(false, root));

        do {
            nodes.forEach(pair -> pair.setFirst(true)); // Mark them that they are about to be processed

            do {
                var pair = nodes.removeFirst();

                if (pair.getFirst()) {
                    var node = pair.getSecond();

                    if (nonNull(node.getValue())) {
                        if (!key.equals(node.getKey())) {
                            entries.add(node.copy());
                        }
                    }

                    node.getNext().entrySet().stream()
                            .sorted((e1, e2) -> compareEntries(e1, e2, asc))
                            .forEach(e -> nodes.addFirst(Pair.of(false, e.getValue())));
                } else {
                    nodes.addFirst(pair);
                    break;
                }
            } while (!nodes.isEmpty());

        } while (!nodes.isEmpty());

        return entries;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getLessThan(TrieNode<K, T> root, K key, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        if (isNull(key)) {
            return get(root, asc);
        }

        var keyStr = String.valueOf(key);

        if (StringUtils.isEmpty(keyStr)) {
            return get(root, asc);
        }

        return getLessThanStr(root, toCharList(keyStr), asc);
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getLessThanEquals(TrieNode<K, T> root, K key, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        if (isNull(key)) {
            return get(root, asc);
        }

        var keyStr = String.valueOf(key);

        if (StringUtils.isEmpty(keyStr)) {
            return get(root, asc);
        }

        return getLessThanEqualsStr(root, toCharList(keyStr), asc);
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBiggerThan(TrieNode<K, T> root, K key, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        if (isNull(key)) {
            return get(root, asc);
        }

        var keyStr = String.valueOf(key);

        if (StringUtils.isEmpty(keyStr)) {
            return get(root, asc);
        }

        return getBiggerThanStr(root, toCharList(keyStr), asc);
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBiggerThanEquals(TrieNode<K, T> root, K key, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        if (isNull(key)) {
            return get(root, asc);
        }

        var keyStr = String.valueOf(key);

        if (StringUtils.isEmpty(keyStr)) {
            return get(root, asc);
        }

        return getBiggerThanEqualsStr(root, toCharList(keyStr), asc);
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBetween(TrieNode<K, T> root, K low, K high, boolean asc) {
        if (isNull(root)) {
            return List.of();
        }

        if (isNull(low) || isNull(high)) {
            return get(root, asc);
        }

        var lowStr = String.valueOf(low);
        var highStr = String.valueOf(high);

        if (StringUtils.isEmpty(lowStr) || StringUtils.isEmpty(highStr)) {
            return get(root, asc);
        }

        return getBetweenStr(root, toCharList(lowStr), toCharList(highStr), asc);
    }

    // TODO remove recursion
    private static <K extends Comparable<K>, T>
    List<Entry<K, T>> getLessThanStr(TrieNode<K, T> node, LinkedList<Character> chars, boolean asc) {
        if (isNull(node)) {
            return List.of();
        }

        var entries = new LinkedList<Entry<K, T>>();

        if (chars.isEmpty()) {
            return List.of();
        }

        char firstChar = chars.removeFirst();

        var nextNodes = node.getNext().entrySet().stream()
                .filter(e -> e.getKey().compareTo(firstChar) <= 0)
                .sorted(Entry.comparingByKey(Collections.reverseOrder()))
                .collect(Collectors.toCollection(LinkedList::new));

        if (nextNodes.isEmpty()) {
            return List.of();
        }

        var firstNode = nextNodes.removeFirst();
        var resultForFirstNode = firstNode.getKey().equals(firstChar) ?
                getLessThanStr(firstNode.getValue(), chars, asc) :
                get(firstNode.getValue(), asc);

        if (asc) {
            reversedView(nextNodes).forEach(e -> entries.addAll(get(e.getValue(), true)));
            entries.addAll(resultForFirstNode);
        } else {
            entries.addAll(resultForFirstNode);
            nextNodes.forEach(e -> entries.addAll(get(e.getValue(), false)));
        }

        return entries;
    }

    private static <K extends Comparable<K>, T>
    List<Entry<K, T>> getLessThanEqualsStr(TrieNode<K, T> node, LinkedList<Character> chars, boolean asc) {
        if (isNull(node)) {
            return List.of();
        }

        var entries = new LinkedList<Entry<K, T>>();

        if (chars.isEmpty()) {
            if (isNull(node.getValue())) {
                return List.of();
            }

            return List.of(node.copy());
        }

        char firstChar = chars.removeFirst();

        var nextNodes = node.getNext().entrySet().stream()
                .filter(e -> e.getKey().compareTo(firstChar) <= 0)
                .sorted(Entry.comparingByKey(Collections.reverseOrder()))
                .collect(Collectors.toCollection(LinkedList::new));

        if (nextNodes.isEmpty()) {
            return List.of();
        }

        var firstNode = nextNodes.removeFirst();
        var resultForFirstNode = firstNode.getKey().equals(firstChar) ?
                getLessThanEqualsStr(firstNode.getValue(), chars, asc) :
                get(firstNode.getValue(), asc);

        if (asc) {
            reversedView(nextNodes).forEach(e -> entries.addAll(get(e.getValue(), true)));
            entries.addAll(resultForFirstNode);
        } else {
            entries.addAll(resultForFirstNode);
            nextNodes.forEach(e -> entries.addAll(get(e.getValue(), false)));
        }

        return entries;
    }

    private static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBiggerThanStr(TrieNode<K, T> node, LinkedList<Character> chars, boolean asc) {
        if (isNull(node)) {
            return List.of();
        }

        if (chars.isEmpty()) {
            return getFromChildren(node, asc);
        } else {
            return traverseForward(
                    node,
                    chars,
                    asc,
                    (firstNode, charsCurrent) -> getBiggerThanStr(firstNode, charsCurrent, asc)
            );
        }
    }

    private static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBiggerThanEqualsStr(TrieNode<K, T> node, LinkedList<Character> chars, boolean asc) {
        if (isNull(node)) {
            return List.of();
        }

        if (chars.isEmpty()) {
            return get(node, asc);
        } else {
            return traverseForward(
                    node,
                    chars,
                    asc,
                    (firstNode, charsCurrent) -> getBiggerThanEqualsStr(firstNode, charsCurrent, asc)
            );
        }
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBetweenStr(TrieNode<K, T> node,
                                    LinkedList<Character> low,
                                    LinkedList<Character> high,
                                    boolean asc) {
        if (isNull(node)) {
            return List.of();
        }

        if (low.isEmpty() || high.isEmpty()) {
            return get(node, asc);
        }

        char firstCharLow = low.removeFirst();
        char firstCharHigh = high.removeFirst();

        var nextNodes = node.getNext().entrySet().stream()
                .filter(e -> e.getKey().compareTo(firstCharLow) >= 0 && e.getKey().compareTo(firstCharHigh) <= 0)
                .sorted(Entry.comparingByKey())
                .collect(Collectors.toCollection(LinkedList::new));

        switch (nextNodes.size()) {
            case 0:
                return List.of();
            case 1: {
                var onlyChildNode = nextNodes.removeFirst().getValue();

                if (isNull(onlyChildNode.getKey()) || onlyChildNode.getKey().equals(firstCharLow) || onlyChildNode.getKey().equals(firstCharHigh)) {
                    return getBetweenStr(onlyChildNode, low, high, asc);
                }

                return get(onlyChildNode, asc);
            }
            default: {
                var entries = new LinkedList<Entry<K, T>>();

                var minNode = nextNodes.removeFirst().getValue();
                var maxNode = nextNodes.removeLast().getValue();

                var lowList = isNull(minNode.getKey()) || minNode.getKey().equals(firstCharLow) ?
                        getBiggerThanEqualsStr(minNode, low, asc) :
                        get(minNode, asc);

                var highList = (nonNull(maxNode.getKey()) && maxNode.getKey().equals(firstCharHigh)) || (isNull(maxNode.getKey()) && high.size() > 1) ?
                        getLessThanEqualsStr(maxNode, high, asc) :
                        get(maxNode, asc);

                if (asc) {
                    entries.addAll(lowList);
                    nextNodes.stream().map(Entry::getValue).forEach(n -> entries.addAll(get(n, true)));
                    entries.addAll(highList);
                } else {
                    entries.addAll(highList);
                    reversedView(nextNodes).stream().map(Entry::getValue).forEach(n -> entries.addAll(get(n, false)));
                    entries.addAll(lowList);
                }

                return entries;
            }
        }
    }

    private static <K extends Comparable<K>, T>
    List<Entry<K, T>> getFromChildren(TrieNode<K, T> node, boolean asc) {
        var entries = new LinkedList<Entry<K, T>>();
        var nextNodes = node.getNext().entrySet().stream()
                .sorted(Entry.comparingByKey())
                .collect(Collectors.toList());

        if (asc) {
            nextNodes.forEach(e -> entries.addAll(get(e.getValue(), true)));
        } else {
            reversedView(nextNodes).forEach(e -> entries.addAll(get(e.getValue(), false)));
        }

        return entries;
    }

    private static <K extends Comparable<K>, T>
    List<Entry<K, T>> traverseForward(TrieNode<K, T> node,
                                      LinkedList<Character> chars,
                                      boolean asc,
                                      BiFunction<TrieNode<K, T>, LinkedList<Character>, List<Entry<K, T>>> forward) {
        var entries = new LinkedList<Entry<K, T>>();
        char firstChar = chars.removeFirst();

        var nextNodes = node.getNext().entrySet().stream()
                .filter(e -> e.getKey().compareTo(firstChar) >= 0)
                .sorted(Entry.comparingByKey())
                .collect(Collectors.toCollection(LinkedList::new));

        if (nextNodes.isEmpty()) {
            return List.of();
        }

        var firstNode = nextNodes.removeFirst();
        var resultForFirstNode = firstNode.getKey().equals(firstChar) ?
                forward.apply(firstNode.getValue(), chars) :
                get(firstNode.getValue(), asc);

        if (asc) {
            entries.addAll(resultForFirstNode);
            nextNodes.forEach(e -> entries.addAll(get(e.getValue(), true)));
        } else {
            reversedView(nextNodes).forEach(e -> entries.addAll(get(e.getValue(), false)));
            entries.addAll(resultForFirstNode);
        }

        return entries;
    }

    private static <T> List<T> reversedView(final List<T> list) {
        return new AbstractList<>() {
            @Override
            public T get(int index) {
                return list.get(list.size() - 1 - index);
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    public static <K extends Comparable<K>, T>
    T delete(TrieNode<K, T> root, K key) {
        if (isNull(root)) {
            return null;
        }

        if (isNull(key)) {
            T data = root.getValue();
            root.setValue(null);
            return data;
        }

        var keyStr = String.valueOf(key);
        var nodeStack = new Stack<Pair<Character, TrieNode<K, T>>>();
        var theOneToDelete = root;

        if (!StringUtils.isEmpty(keyStr)) {
            var current = root;

            for (char c : keyStr.toCharArray()) {
                if (isNull(current)) {
                    return null;
                } else {
                    nodeStack.push(Pair.of(c, current));
                    current = current.getNext().getOrDefault(c, null);
                    theOneToDelete = current;
                }
            }
        }

        if (!theOneToDelete.hasNext()) {
            while (!nodeStack.isEmpty()) {
                var characterToNode = nodeStack.pop();
                var c = characterToNode.getFirst();
                var node = characterToNode.getSecond();

                node.getNext().remove(c);

                if (!node.isEmpty()) {
                    break;
                }
            }
        }

        theOneToDelete.setKey(null);

        return theOneToDelete.setValue(null);
    }

    public static <T, K extends Comparable<K>>
    Collection<T> values(TrieNode<K, T> root) {
        var stack = new Stack<TrieNode<K, T>>();
        stack.push(root);

        var resultList = new LinkedList<T>();

        while (!stack.isEmpty()) {
            var current = stack.pop();
            if (nonNull(current.getValue())) {
                resultList.add(current.getValue());
            }
            current.getNext().values().forEach(stack::push);
        }

        return resultList;
    }

    public static <T, K extends Comparable<K>>
    Set<Entry<K, T>> entrySet(TrieNode<K, T> root) {
        var stack = new Stack<TrieNode<K, T>>();
        stack.push(root);

        var resultSet = new HashSet<Entry<K, T>>();

        while (!stack.isEmpty()) {
            var current = stack.pop();
            if (nonNull(current.getValue())) {
                resultSet.add(current.copy());
            }
            current.getNext().values().forEach(stack::push);
        }

        return resultSet;
    }

    public static <K extends Comparable<K>, T>
    Set<K> keySet(TrieNode<K, T> root) {
        var stack = new Stack<TrieNode<K, T>>();
        stack.push(root);

        var resultSet = new HashSet<K>();

        while (!stack.isEmpty()) {
            var current = stack.pop();
            if (nonNull(current.getKey())) {
                resultSet.add(current.getKey());
            }
            current.getNext().values().forEach(stack::push);
        }

        return resultSet;
    }

    private static <K extends Comparable<K>, T>
    int compareEntries(Entry<Character, TrieNode<K, T>> e1, Entry<Character, TrieNode<K, T>> e2, boolean asc) {
        Character k1 = asc ? e1.getKey() : e2.getKey();
        Character k2 = asc ? e2.getKey() : e1.getKey();
        return k2.compareTo(k1);
    }

    public static <K extends Comparable<K>, T>
    K getMin(TrieNode<K, T> root) {
        if (isNull(root)) {
            return null;
        }

        var current = root;

        while (nonNull(current) && current.hasNext()) {
            current = nextMin(current);
        }

        if (isNull(current)) {
            return null;
        }

        return current.getKey();
    }

    public static <K extends Comparable<K>, T>
    K getMax(TrieNode<K, T> root) {
        if (isNull(root)) {
            return null;
        }

        var current = root;

        while (nonNull(current) && current.hasNext()) {
            current = nextMax(current);
        }

        if (isNull(current)) {
            return null;
        }

        return current.getKey();
    }

    private static <K extends Comparable<K>, T>
    TrieNode<K, T> nextMin(TrieNode<K, T> node) {
        var next = node.getNext();

        return next.keySet().stream()
                .min(Character::compareTo)
                .map(next::get)
                .orElse(null);
    }

    private static <K extends Comparable<K>, T>
    TrieNode<K, T> nextMax(TrieNode<K, T> node) {
        var next = node.getNext();

        return next.keySet().stream()
                .max(Character::compareTo)
                .map(next::get)
                .orElse(null);
    }

    public static LinkedList<Character> toCharList(String s) {
        return s.chars().boxed()
                .map(i -> (char) i.intValue())
                .collect(Collectors.toCollection(LinkedList::new));
    }
}