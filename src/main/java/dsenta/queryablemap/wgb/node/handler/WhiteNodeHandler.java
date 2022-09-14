package dsenta.queryablemap.wgb.node.handler;

import static dsenta.queryablemap.wgb.node.handler.WgbNodeHandler.collectFromGreyNodes;
import static dsenta.queryablemap.wgb.node.util.OrderListFlatter.flatAsc;
import static dsenta.queryablemap.wgb.node.util.OrderListFlatter.flatDesc;
import static java.util.Objects.isNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import dsenta.queryablemap.exception.NotFoundException;
import dsenta.queryablemap.exception.UniqueException;
import dsenta.queryablemap.wgb.node.model.GreyNode;
import dsenta.queryablemap.wgb.node.model.PopResult;
import dsenta.queryablemap.wgb.node.model.WgbData;
import dsenta.queryablemap.wgb.node.model.WgbKey;
import dsenta.queryablemap.wgb.node.model.WhiteNode;
import dsenta.queryablemap.wgb.node.util.Prime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WhiteNodeHandler {

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMin(WhiteNode<K, T> whiteNode) {
        return Objects.nonNull(whiteNode) ? whiteNode.getData() : null;
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, WhiteNode<K, T>> popMin(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new PopResult<>(null, null);
        }

        var indexDataResult = WgbNodeHandler.getChildNodeWithMinData(whiteNode);
        WgbData<K, T> min = whiteNode.getData();

        if (isNull(indexDataResult.getIndex())) {
            return new PopResult<>(null, min);
        } else {
            var popResult = GreyNodeHandler.popMin(whiteNode.get(indexDataResult.getIndex()));
            whiteNode.setNode(indexDataResult.getIndex(), popResult.getNode());
            whiteNode.setData(indexDataResult.getData());
            WgbNodeHandler.calculateAndSetDepth(whiteNode);

            return new PopResult<>(whiteNode, min);
        }
    }

    public static <K extends Comparable<K>, T>
    WhiteNode<K, T> delete(WhiteNode<K, T> whiteNode, WgbKey<K> key) throws NotFoundException, UniqueException {
        if (isNull(whiteNode)) {
            throw new NotFoundException();
        }

        int cmp = key.compareTo(whiteNode.getKey());

        if (cmp < 0) {
            throw new NotFoundException();
        } else if (cmp > 0) {
            int index = whiteNode.nextIndex(key);
            whiteNode.setNode(index, GreyNodeHandler.delete(whiteNode.get(index), key));
        } else {
            var indexDataResult = WgbNodeHandler.getChildNodeWithMinData(whiteNode);

            if (isNull(indexDataResult.getIndex())) {
                return null;
            } else {
                var popResult = GreyNodeHandler.popMin(whiteNode.get(indexDataResult.getIndex()));
                whiteNode.setNode(indexDataResult.getIndex(), popResult.getNode());
                whiteNode.setData(indexDataResult.getData());
                WgbNodeHandler.calculateAndSetDepth(whiteNode);
            }
        }

        return whiteNode;
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, WhiteNode<K, T>> popMax(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new PopResult<>(null, null);
        }

        var indexDataResult = WgbNodeHandler.getChildNodeWithMaxData(whiteNode);

        if (isNull(indexDataResult.getIndex())) {
            return new PopResult<>(null, whiteNode.getData());
        } else {
            var popResult = GreyNodeHandler.popMax(whiteNode.get(indexDataResult.getIndex()));
            whiteNode.setNode(indexDataResult.getIndex(), popResult.getNode());
            WgbNodeHandler.calculateAndSetDepth(whiteNode);

            return new PopResult<>(whiteNode, indexDataResult.getData());
        }
    }

    public static <K extends Comparable<K>, T>
    WhiteNode<K, T> insert(WhiteNode<K, T> whiteNode, int capacity, WgbData<K, T> data) throws UniqueException {
        if (isNull(whiteNode)) {
            return new WhiteNode<>(data, capacity);
        }

        int cmp = data.getKey().compareTo(whiteNode.getKey());

        if (cmp < 0) {
            WgbData<K, T> nodeData = whiteNode.getData();

            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    whiteNode.next(nodeData.getKey()),
                    Prime.nextPrime(capacity),
                    nodeData
            );

            if (greyNode.getDepth() >= whiteNode.getDepth()) {
                whiteNode.setDepth(greyNode.getDepth() + 1);
            }

            whiteNode.setNode(whiteNode.nextIndex(greyNode.getKey()), greyNode);
            whiteNode.setData(data);
        } else if (cmp > 0) {
            GreyNode<K, T> greyNode = GreyNodeHandler.insert(
                    whiteNode.next(data.getKey()),
                    Prime.nextPrime(capacity),
                    data
            );

            if (greyNode.getDepth() >= whiteNode.getDepth()) {
                whiteNode.setDepth(greyNode.getDepth() + 1);
            }

            whiteNode.setNode(whiteNode.nextIndex(greyNode.getKey()), greyNode);
        } else {
            throw new UniqueException(String.format("Data %s already exists!", data));
        }

        return whiteNode;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getAsc(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, GreyNodeHandler::getAsc);
        var result = new LinkedList<Entry<K, T>>();
        var whiteNodeData = whiteNode.getData();
        result.add(Map.entry(whiteNodeData.getKey().getValue(), whiteNodeData.getValue()));
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getDesc(WhiteNode<K, T> whiteNode) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, GreyNodeHandler::getDesc);
        var result = flatDesc(inOrderLists);
        result.add(whiteNode.toMapEntry());

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getBiggerThanAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything asc

            return getAsc(whiteNode);
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getBiggerThanAsc(node, key));

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything desc
            return getDesc(whiteNode);
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getBiggerThanDesc(node, key));

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // white node is bigger or equals key
            // all child nodes are also bigger or equals key
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getLessThanAsc(node, key));
        var result = new LinkedList<Entry<K, T>>();
        result.add(whiteNode.toMapEntry());
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getLessThanDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // white node is bigger or equals key
            // all child nodes are also bigger or equals key
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getLessThanDesc(node, key));
        var result = flatDesc(inOrderLists);
        result.add(whiteNode.toMapEntry());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanEqualsAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything asc
            return getAsc(whiteNode);
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getBiggerThanEqualsAsc(node, key));

        return flatAsc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanEqualsDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp >= 0) {
            // This node and all nodes below are surely bigger than key
            // therefore return everything asc
            return getDesc(whiteNode);
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getBiggerThanEqualsDesc(node, key));

        return flatDesc(inOrderLists);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanEqualsAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // white node is bigger than key
            // all child nodes are also than key
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getLessThanEqualsAsc(node, key));
        var result = new LinkedList<Entry<K, T>>();
        result.add(whiteNode.toMapEntry());
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanEqualsDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            // white node is bigger than key
            // all child nodes are also than key
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getLessThanEqualsDesc(node, key));
        var result = flatDesc(inOrderLists);
        result.add(whiteNode.toMapEntry());

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBetweenAsc(WhiteNode<K, T> whiteNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmpHigh = whiteNode.getKey().compareTo(high);

        if (cmpHigh > 0) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getBetweenAsc(node, low, high));
        var result = new LinkedList<Entry<K, T>>();
        int cmpLow = whiteNode.getKey().compareTo(low);
        if (cmpLow >= 0) {
            result.add(whiteNode.toMapEntry());
        }
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBetweenDesc(WhiteNode<K, T> whiteNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmpHigh = whiteNode.getKey().compareTo(high);

        if (cmpHigh > 0) {
            return new LinkedList<>();
        }

        var inOrderLists = collectFromGreyNodes(whiteNode, node -> GreyNodeHandler.getBetweenDesc(node, low, high));
        var result = flatDesc(inOrderLists);
        int cmpLow = whiteNode.getKey().compareTo(low);
        if (cmpLow >= 0) {
            result.add(whiteNode.toMapEntry());
        }

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getNotEqualsAsc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            return getAsc(whiteNode);
        }

        int carefulIndex = whiteNode.nextIndex(key);
        List<List<Entry<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsAsc(whiteNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getAsc(whiteNode.get(i)));
            }
        }

        List<Entry<K, T>> result = new LinkedList<>();
        if (cmp != 0) {
            result.add(whiteNode.toMapEntry());
        }
        result.addAll(flatAsc(inOrderLists));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getNotEqualsDesc(WhiteNode<K, T> whiteNode, WgbKey<K> key) {
        if (isNull(whiteNode)) {
            return new LinkedList<>();
        }

        int cmp = whiteNode.getKey().compareTo(key);

        if (cmp > 0) {
            return getDesc(whiteNode);
        }

        int carefulIndex = whiteNode.nextIndex(key);
        List<List<Entry<K, T>>> inOrderLists = new LinkedList<>();

        for (int i = 0; i < whiteNode.getCapacity(); i++) {
            if (i == carefulIndex) {
                inOrderLists.add(GreyNodeHandler.getNotEqualsDesc(whiteNode.get(i), key));
            } else {
                inOrderLists.add(GreyNodeHandler.getDesc(whiteNode.get(i)));
            }
        }

        List<Entry<K, T>> result = flatDesc(inOrderLists);
        if (cmp != 0) {
            result.add(whiteNode.toMapEntry());
        }

        return result;
    }
}