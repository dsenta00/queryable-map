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

import static dsenta.queryablemap.wgb.node.handler.GreyNodeHandler.BalanceResult.GOOD;
import static dsenta.queryablemap.wgb.node.handler.GreyNodeHandler.BalanceResult.MORE_LEFT;
import static dsenta.queryablemap.wgb.node.handler.GreyNodeHandler.BalanceResult.MORE_RIGHT;
import static dsenta.queryablemap.wgb.node.handler.WgbNodeHandler.calculateAndSetDepth;
import static dsenta.queryablemap.wgb.node.handler.WgbNodeHandler.depth;
import static java.util.Objects.isNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import dsenta.queryablemap.exception.NotFoundException;
import dsenta.queryablemap.exception.UniqueException;
import dsenta.queryablemap.trie.node.model.Pair;
import dsenta.queryablemap.wgb.node.model.BlackNode;
import dsenta.queryablemap.wgb.node.model.GreyNode;
import dsenta.queryablemap.wgb.node.model.PopResult;
import dsenta.queryablemap.wgb.node.model.WgbData;
import dsenta.queryablemap.wgb.node.model.WgbKey;
import dsenta.queryablemap.wgb.node.model.WhiteNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class GreyNodeHandler {

    private static final int MAX_NO_OF_ROTATIONS = 10;
    private static final int DEPTH_THRESHOLD = 2;
    private static final int DEPTH_THRESHOLD_TO_RELATIVE_CALCULATION = 5;
    private static final float DEPTH_THRESHOLD_RELATIVE = 1.25f;
    private static final float DEPTH_THRESHOLD_RELATIVE_INVERSE = 1 / DEPTH_THRESHOLD_RELATIVE;

    public enum BalanceResult {
        MORE_LEFT,
        MORE_RIGHT,
        GOOD
    }

    public static <K extends Comparable<K>, T>
    void printDepth(GreyNode<K, T> greyNode, int depth) {
        if (isNull(greyNode)) {
            System.out.println("null");
            return;
        }

        System.out.println(greyNode.getKey());

        if (Objects.nonNull(greyNode.getWhiteNode())) {
            for (int i = 0; i < depth + 2; i++) {
                System.out.print(" ");
            }
            System.out.print("L: ");
            WgbNodeHandler.printDepth(greyNode.getWhiteNode(), depth + 4);
        }

        if (Objects.nonNull(greyNode.getBlackNode())) {
            for (int i = 0; i < depth + 2; i++) {
                System.out.print(" ");
            }
            System.out.print("R: ");
            WgbNodeHandler.printDepth(greyNode.getBlackNode(), depth + 4);
        }
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getAsc(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        List<Entry<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
        result.add(greyNode.toMapEntry());
        result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));

        return result;
    }

    public static <K extends Comparable<K>, T>
    List<Entry<K, T>> getDesc(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        List<Entry<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
        result.add(greyNode.toMapEntry());
        result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));

        return result;
    }

    public static <K extends Comparable<K>, T>
    GreyNode<K, T> delete(GreyNode<K, T> greyNode, WgbKey<K> key) throws NotFoundException, UniqueException {
        if (isNull(greyNode)) {
            throw new NotFoundException();
        }

        int cmp = key.compareTo(greyNode.getKey());

        if (cmp < 0) {
            WhiteNode<K, T> whiteNode = WhiteNodeHandler.delete(greyNode.getWhiteNode(), key);
            greyNode.setWhiteNode(whiteNode);
        } else if (cmp > 0) {
            BlackNode<K, T> blackNode = BlackNodeHandler.delete(greyNode.getBlackNode(), key);
            greyNode.setBlackNode(blackNode);
        } else {
            WhiteNode<K, T> whiteNode = greyNode.getWhiteNode();
            BlackNode<K, T> blackNode = greyNode.getBlackNode();

            if (isNull(whiteNode)) {
                PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMin(blackNode);

                if (isNull(popResult.getData())) {
                    return null;
                }

                greyNode.setData(popResult.getData());
                greyNode.setBlackNode(popResult.getNode());
            } else {
                PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMax(whiteNode);

                if (isNull(popResult.getData())) {
                    return null;
                }

                greyNode.setData(popResult.getData());
                greyNode.setWhiteNode(popResult.getNode());
            }
        }

        calculateAndSetDepth(greyNode);
        return rotateUntilBalance(greyNode);
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMin(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return null;
        }

        WgbData<K, T> min = WhiteNodeHandler.getMin(greyNode.getWhiteNode());

        return Objects.nonNull(min) ? min : greyNode.getData();
    }

    public static <K extends Comparable<K>, T>
    WgbData<K, T> getMax(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return null;
        }

        WgbData<K, T> max = BlackNodeHandler.getMax(greyNode.getBlackNode());

        return Objects.nonNull(max) ? max : greyNode.getData();
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, GreyNode<K, T>> popMin(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new PopResult<>(null, null);
        }

        WhiteNode<K, T> whiteNode = greyNode.getWhiteNode();

        if (isNull(whiteNode)) {
            WgbData<K, T> min = greyNode.getData();

            PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMin(greyNode.getBlackNode());

            if (isNull(popResult.getData())) {
                return new PopResult<>(null, min);
            }

            greyNode.setBlackNode(popResult.getNode());
            greyNode.setData(popResult.getData());
            calculateAndSetDepth(greyNode);

            return new PopResult<>(greyNode, min);
        }

        PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMin(whiteNode);
        greyNode.setWhiteNode(popResult.getNode());
        calculateAndSetDepth(greyNode);

        return new PopResult<>(greyNode, popResult.getData());
    }

    public static <K extends Comparable<K>, T>
    PopResult<K, T, GreyNode<K, T>> popMax(GreyNode<K, T> greyNode) {
        if (isNull(greyNode)) {
            return new PopResult<>(null, null);
        }

        BlackNode<K, T> blackNode = greyNode.getBlackNode();

        if (isNull(blackNode)) {
            WgbData<K, T> max = greyNode.getData();
            PopResult<K, T, WhiteNode<K, T>> popResult = WhiteNodeHandler.popMax(greyNode.getWhiteNode());

            if (isNull(popResult.getData())) {
                return new PopResult<>(null, max);
            }

            greyNode.setWhiteNode(popResult.getNode());
            greyNode.setData(popResult.getData());
            calculateAndSetDepth(greyNode);

            return new PopResult<>(greyNode, max);
        }

        PopResult<K, T, BlackNode<K, T>> popResult = BlackNodeHandler.popMax(blackNode);
        greyNode.setBlackNode(popResult.getNode());

        int leftDepth = depth(greyNode.getWhiteNode());
        int rightDepth = depth(greyNode.getBlackNode());
        greyNode.setDepth(leftDepth > rightDepth ? leftDepth + 1 : rightDepth + 1);

        return new PopResult<>(greyNode, popResult.getData());
    }

    public static <K extends Comparable<K>, T>
    GreyNode<K, T> rotateUntilBalance(GreyNode<K, T> greyNode) throws UniqueException {
        if (isNull(greyNode)) {
            return null;
        }

        for (int i = 0; i < MAX_NO_OF_ROTATIONS; i++) {
            var rotationResult = rotate(greyNode);
            greyNode = rotationResult.getFirst();
            if (GOOD.equals(rotationResult.getSecond())) {
                return greyNode;
            }
        }

        return greyNode;
    }

    public static <K extends Comparable<K>, T>
    Pair<GreyNode<K, T>, BalanceResult> rotate(GreyNode<K, T> greyNode) throws UniqueException {
        if (isNull(greyNode)) {
            return Pair.of(null, GOOD);
        }

        var whiteNode = greyNode.getWhiteNode();
        var blackNode = greyNode.getBlackNode();

        if (isNull(whiteNode) && isNull(blackNode)) {
            greyNode.setDepth(1);
            return Pair.of(greyNode, GOOD);
        }

        var balanceResult = getBalanceResult(depth(whiteNode), depth(blackNode));

        switch (balanceResult) {
            case MORE_LEFT: {
                var popResult = WhiteNodeHandler.popMax(whiteNode);
                greyNode.setWhiteNode(popResult.getNode());
                greyNode.setBlackNode(BlackNodeHandler.insert(blackNode, whiteNode.getCapacity(), greyNode.getData()));
                greyNode.setData(popResult.getData());
                break;
            }
            case MORE_RIGHT: {
                var popResult = BlackNodeHandler.popMin(blackNode);
                greyNode.setBlackNode(popResult.getNode());
                greyNode.setWhiteNode(WhiteNodeHandler.insert(whiteNode, blackNode.getCapacity(), greyNode.getData()));
                greyNode.setData(popResult.getData());
                break;
            }
            case GOOD: {
                return Pair.of(greyNode, GOOD);
            }
        }

        calculateAndSetDepth(greyNode);

        return Pair.of(greyNode, balanceResult);
    }

    public static <K extends Comparable<K>, T>
    GreyNode<K, T> insert(GreyNode<K, T> greyNode, int capacity, WgbData<K, T> data) throws UniqueException {
        if (isNull(greyNode)) {
            return new GreyNode<>(data);
        }

        int cmp = data.getKey().compareTo(greyNode.getKey());

        if (cmp < 0) {
            var whiteNode = WhiteNodeHandler.insert(greyNode.getWhiteNode(), capacity, data);

            if (whiteNode.getDepth() >= greyNode.getDepth()) {
                greyNode.setDepth(whiteNode.getDepth() + 1);
            }

            greyNode.setWhiteNode(whiteNode);
        } else if (cmp > 0) {
            var blackNode = BlackNodeHandler.insert(greyNode.getBlackNode(), capacity, data);

            if (blackNode.getDepth() >= greyNode.getDepth()) {
                greyNode.setDepth(blackNode.getDepth() + 1);
            }

            greyNode.setBlackNode(blackNode);
        } else {
            throw new UniqueException(String.format("Data %s already exists!", data));
        }

        calculateAndSetDepth(greyNode);

        return rotateUntilBalance(greyNode);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp > 0) {
            var dataList = WhiteNodeHandler.getBiggerThanAsc(greyNode.getWhiteNode(), key);
            dataList.add(greyNode.toMapEntry());
            dataList.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return dataList;
        }

        return BlackNodeHandler.getBiggerThanAsc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp > 0) {
            var dataList = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            dataList.add(greyNode.toMapEntry());
            dataList.addAll(WhiteNodeHandler.getBiggerThanDesc(greyNode.getWhiteNode(), key));
            return dataList;
        }

        return BlackNodeHandler.getBiggerThanDesc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp < 0) {
            var dataList = WhiteNodeHandler.getDesc(greyNode.getWhiteNode());
            dataList.add(greyNode.toMapEntry());
            dataList.addAll(BlackNodeHandler.getLessThanAsc(greyNode.getBlackNode(), key));
            return dataList;
        }

        return WhiteNodeHandler.getLessThanAsc(greyNode.getWhiteNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp < 0) {
            List<Entry<K, T>> result = BlackNodeHandler.getLessThanDesc(greyNode.getBlackNode(), key);
            result.add(greyNode.toMapEntry());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        }

        return WhiteNodeHandler.getLessThanDesc(greyNode.getWhiteNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanEqualsAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp >= 0) {
            List<Entry<K, T>> result = WhiteNodeHandler.getBiggerThanEqualsAsc(greyNode.getWhiteNode(), key);
            result.add(greyNode.toMapEntry());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        }

        return BlackNodeHandler.getBiggerThanEqualsAsc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBiggerThanEqualsDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp >= 0) {
            List<Entry<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.add(greyNode.toMapEntry());
            result.addAll(WhiteNodeHandler.getBiggerThanEqualsDesc(greyNode.getWhiteNode(), key));
            return result;
        }

        return BlackNodeHandler.getBiggerThanEqualsDesc(greyNode.getBlackNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanEqualsAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp <= 0) {
            List<Entry<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
            result.add(greyNode.toMapEntry());
            result.addAll(BlackNodeHandler.getLessThanEqualsAsc(greyNode.getBlackNode(), key));
            return result;
        }

        return WhiteNodeHandler.getLessThanEqualsAsc(greyNode.getWhiteNode(), key);
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getLessThanEqualsDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp <= 0) {
            List<Entry<K, T>> result = BlackNodeHandler.getLessThanEqualsDesc(greyNode.getBlackNode(), key);
            result.add(greyNode.toMapEntry());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        }

        return WhiteNodeHandler.getLessThanEqualsDesc(greyNode.getWhiteNode(), key);

    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBetweenAsc(GreyNode<K, T> greyNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmpLow = greyNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            // go right
            return BlackNodeHandler.getBetweenAsc(greyNode.getBlackNode(), low, high);
        }

        List<Entry<K, T>> result = WhiteNodeHandler.getBetweenAsc(greyNode.getWhiteNode(), low, high);
        int cmpHigh = greyNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(greyNode.toMapEntry());
        }
        result.addAll(BlackNodeHandler.getBetweenAsc(greyNode.getBlackNode(), low, high));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getBetweenDesc(GreyNode<K, T> greyNode, WgbKey<K> low, WgbKey<K> high) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmpLow = greyNode.getKey().compareTo(low);

        if (cmpLow < 0) {
            // go right
            return BlackNodeHandler.getBetweenDesc(greyNode.getBlackNode(), low, high);
        }

        List<Entry<K, T>> result = BlackNodeHandler.getBetweenDesc(greyNode.getBlackNode(), low, high);
        int cmpHigh = greyNode.getKey().compareTo(high);
        if (cmpHigh <= 0) {
            result.add(greyNode.toMapEntry());
        }
        result.addAll(WhiteNodeHandler.getBetweenDesc(greyNode.getWhiteNode(), low, high));

        return result;
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getNotEqualsAsc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp == 0) {
            List<Entry<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        } else if (cmp < 0) {
            List<Entry<K, T>> result = WhiteNodeHandler.getAsc(greyNode.getWhiteNode());
            result.add(greyNode.toMapEntry());
            result.addAll(BlackNodeHandler.getNotEqualsAsc(greyNode.getBlackNode(), key));
            return result;
        } else {
            List<Entry<K, T>> result = WhiteNodeHandler.getNotEqualsAsc(greyNode.getWhiteNode(), key);
            result.add(greyNode.toMapEntry());
            result.addAll(BlackNodeHandler.getAsc(greyNode.getBlackNode()));
            return result;
        }
    }

    public static <T, K extends Comparable<K>>
    List<Entry<K, T>> getNotEqualsDesc(GreyNode<K, T> greyNode, WgbKey<K> key) {
        if (isNull(greyNode)) {
            return new LinkedList<>();
        }

        int cmp = greyNode.getKey().compareTo(key);

        if (cmp == 0) {
            List<Entry<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        } else if (cmp < 0) {
            List<Entry<K, T>> result = BlackNodeHandler.getNotEqualsDesc(greyNode.getBlackNode(), key);
            result.add(greyNode.toMapEntry());
            result.addAll(WhiteNodeHandler.getDesc(greyNode.getWhiteNode()));
            return result;
        } else {
            List<Entry<K, T>> result = BlackNodeHandler.getDesc(greyNode.getBlackNode());
            result.add(greyNode.toMapEntry());
            result.addAll(WhiteNodeHandler.getNotEqualsDesc(greyNode.getWhiteNode(), key));
            return result;
        }
    }

    public static BalanceResult getBalanceResult(int left, int right) {
        if (left <= DEPTH_THRESHOLD_TO_RELATIVE_CALCULATION && right <= DEPTH_THRESHOLD_TO_RELATIVE_CALCULATION) {
            int diff = left - right;

            if (diff < -DEPTH_THRESHOLD) {
                return MORE_RIGHT;
            } else if (diff > DEPTH_THRESHOLD) {
                return MORE_LEFT;
            } else {
                return GOOD;
            }
        }

        double leftToRight = ((float) left) / right;

        if (leftToRight > DEPTH_THRESHOLD_RELATIVE) {
            return MORE_LEFT;
        } else if (leftToRight < DEPTH_THRESHOLD_RELATIVE_INVERSE) {
            return MORE_RIGHT;
        }

        return GOOD;
    }
}
