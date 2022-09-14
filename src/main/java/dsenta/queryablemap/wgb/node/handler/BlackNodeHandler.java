package dsenta.queryablemap.wgb.node.handler;

import static dsenta.queryablemap.wgb.node.handler.WgbNodeHandler.collectFromGreyNodes;
import static dsenta.queryablemap.wgb.node.util.OrderListFlatter.flatAsc;
import static dsenta.queryablemap.wgb.node.util.OrderListFlatter.flatDesc;
import static java.util.Objects.isNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import dsenta.queryablemap.exception.NotFoundException;
import dsenta.queryablemap.exception.UniqueException;
import dsenta.queryablemap.wgb.node.model.BlackNode;
import dsenta.queryablemap.wgb.node.model.GreyNode;
import dsenta.queryablemap.wgb.node.model.PopResult;
import dsenta.queryablemap.wgb.node.model.WgbData;
import dsenta.queryablemap.wgb.node.model.WgbKey;
import dsenta.queryablemap.wgb.node.util.Prime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlackNodeHandler {

    public static <K extends Comparable<K>, T>
    BlackNode<K, T> delete(BlackNode<K, T> blackNode, WgbKey<K> key) throws NotFoundException, UniqueException {
        if (Objects.isNull(blackNode)) {
            throw new NotFoundException();
        }

        int cmp = key.getValue().compareTo(blackNode.getKey().getValue());

        if (cmp > 0) {
            throw new NotFoundException();

        } else if (cmp < 0) {
            int index = blackNode.nextIndex(key);
            blackNode.setNode(index, GreyNodeHandler.delete(blackNode.get(index), key));
        } else {
            var indexDataResult = WgbNodeHandler.getChildNodeWithMaxData(blackNode);

            if (isNull(indexDataResult.getIndex())) {
                return null;
            } else {
                var popResult = GreyNodeHandler.popMax(blackNode.get(indexDataResult.getIndex()));
                blackNode.setNode(indexDataResult.getIndex(), popResult.getNode());
                blackNode.setData(indexDataResult.getData());
                WgbNodeHandler.calculateAndSetDepth(blackNode);
            }
        }

        return blackNode;
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, BlackNode<K, T>> popMax(BlackNode<K, T> blackNode) {
        if (isNull(blackNode)) {
            return new PopResult<>(null, null);
        }

        var indexDataResult = WgbNodeHandler.getChildNodeWithMaxData(blackNode);

        if (isNull(indexDataResult.getIndex())) {
            return new PopResult<>(null, blackNode.getData());
        } else {
            PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMax(blackNode.get(indexDataResult.getIndex()));
            blackNode.setNode(indexDataResult.getIndex(), popResult.getNode());
            blackNode.setData(indexDataResult.getData());
            WgbNodeHandler.calculateAndSetDepth(blackNode);

            return new PopResult<>(blackNode, blackNode.getData());
        }
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, BlackNode<K, T>> popMin(BlackNode<K, T> blackNode) {
        if (isNull(blackNode)) {
            return new PopResult<>(null, null);
        }

        var indexDataResult = WgbNodeHandler.getChildNodeWithMinData(blackNode);

        if (isNull(indexDataResult.getIndex())) {
            return new PopResult<>(null, blackNode.getData());
        } else {
            PopResult<K, T, GreyNode<K, T>> popResult = GreyNodeHandler.popMin(blackNode.get(indexDataResult.getIndex()));
            blackNode.setNode(indexDataResult.getIndex(), popResult.getNode());
            WgbNodeHandler.calculateAndSetDepth(blackNode);

            return new PopResult<>(blackNode, indexDataResult.getData());
        }
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMax(BlackNode<K, T> blackNode) {
        return Objects.nonNull(blackNode) ? blackNode.getData() : null;
    }

    public static <K extends Comparable<K>, T>
    BlackNode<K, T> insert(BlackNode<K, T> blackNode, int capacity, WgbData<K, T> data) throws UniqueException {
        if (isNull(blackNode)) {
            return new BlackNode<>(data, capacity);
        }

        int cmp = data.getKey().compareTo(blackNode.getKey());

        if (cmp > 0) {
            WgbData<K, T> nodeData = blackNode.getData();

            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    blackNode.next(nodeData.getKey()),
                    Prime.nextPrime(capacity),
                    nodeData
            );

            if (greyNode.getDepth() >= blackNode.getDepth()) {
                blackNode.setDepth(greyNode.getDepth() + 1);
            }

            blackNode.setNode(blackNode.nextIndex(greyNode.getKey()), greyNode);
            blackNode.setData(data);
        } else if (cmp < 0) {
            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    blackNode.next(data.getKey()),
                    Prime.nextPrime(capacity),
                    data
            );

            if (greyNode.getDepth() >= blackNode.getDepth()) {
                blackNode.setDepth(greyNode.getDepth() + 1);
            }

            blackNode.setNode(blackNode.nextIndex(greyNode.getKey()), greyNode);
        } else {
            throw new UniqueException(String.format("Data %s already exists!", data));
        }

        return blackNode;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getAsc(BlackNode<K, T> blackNode) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(blackNode, GreyNodeHandler::getAsc);
        var result = flatAsc(inOrderLists);
        result.add(blackNode.toMapEntry());

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getDesc(BlackNode<K, T> blackNode) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(blackNode, GreyNodeHandler::getDesc);

        List<Entry<K, T>> result = new LinkedList<>();
        result.add(blackNode.toMapEntry());
        result.addAll(flatDesc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBiggerThanAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            // since black node is smaller or equals key
            // all child nodes are also smaller or equals key
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getBiggerThanAsc(greyNode, key));

        List<Entry<K, T>> result = flatAsc(inOrderLists);
        result.add(blackNode.toMapEntry());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            // since black node is smaller or equals key
            // all child nodes are also smaller or equals key
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getBiggerThanDesc(greyNode, key));

        List<Entry<K, T>> result = new LinkedList<>();
        result.add(blackNode.toMapEntry());
        result.addAll(flatDesc(inOrderLists));

        return result;

    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getAsc(blackNode);
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getLessThanAsc(greyNode, key));

        return flatAsc(inOrderLists);
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getLessThanDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getDesc(blackNode);
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getLessThanDesc(greyNode, key));

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanEqualsAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            // since black node is smaller than key
            // all child nodes are also smaller than key
            return new LinkedList<>();
        } else if (cmp == 0) {
            // only black node fits
            List<Entry<K, T>> result = new LinkedList<>();
            result.add(blackNode.toMapEntry());
            return result;
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getBiggerThanEqualsAsc(greyNode, key));

        List<Entry<K, T>> result = flatAsc(inOrderLists);
        result.add(blackNode.toMapEntry());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanEqualsDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            // since black node is smaller than key
            // all child nodes are also smaller than key
            return new LinkedList<>();
        } else if (cmp == 0) {
            // only black node fits
            List<Entry<K, T>> result = new LinkedList<>();
            result.add(blackNode.toMapEntry());
            return result;
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getBiggerThanEqualsDesc(greyNode, key));

        List<Entry<K, T>> result = new LinkedList<>();
        result.add(blackNode.toMapEntry());
        result.addAll(flatDesc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanEqualsAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            return getAsc(blackNode);
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getLessThanEqualsAsc(greyNode, key));

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanEqualsDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp <= 0) {
            return getDesc(blackNode);
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getLessThanEqualsDesc(greyNode, key));
        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBetweenAsc(BlackNode<K, T> blackNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmpLow = blackNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getBetweenAsc(greyNode, low, high));
        var result = flatAsc(inOrderLists);
        int cmpHigh = blackNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(blackNode.toMapEntry());
        }

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBetweenDesc(BlackNode<K, T> blackNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmpLow = blackNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(blackNode, greyNode -> GreyNodeHandler.getBetweenDesc(greyNode, low, high));
        var result = new LinkedList<Entry<K, T>>();
        int cmpHigh = blackNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(blackNode.toMapEntry());
        }
        result.addAll(flatDesc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getNotEqualsAsc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getAsc(blackNode);
        }

        int carefulIndex = blackNode.nextIndex(key);

        List<List<Entry<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsAsc(blackNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getAsc(blackNode.get(i)));
            }
        }

        List<Entry<K, T>> result = new LinkedList<>(flatAsc(inOrderLists));
        if (cmp != 0) {
            result.add(blackNode.toMapEntry());
        }

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getNotEqualsDesc(BlackNode<K, T> blackNode, WgbKey<K> key) {
        if (isNull(blackNode)) {
            return new LinkedList<>();
        }

        int cmp = blackNode.getKey().compareTo(key);

        if (cmp < 0) {
            return getDesc(blackNode);
        }

        int carefulIndex = blackNode.nextIndex(key);

        List<List<Entry<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < blackNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsDesc(blackNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getDesc(blackNode.get(i)));
            }
        }

        List<Entry<K, T>> result = new LinkedList<>();
        if (cmp != 0) {
            result.add(blackNode.toMapEntry());
        }
        result.addAll(flatDesc(inOrderLists));

        return result;
    }
}