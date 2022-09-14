package dsenta.queryablemap.wgb.node.util;

import dsenta.queryablemap.exception.ShouldNeverHappenException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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