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

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GreyNode<K extends Comparable<K>, T> extends WgbNode<K, T> implements Serializable {
    private static final long serialVersionUID = 1083948174594508322L;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOTAL = 2;

    public GreyNode(WgbData<K, T> data) {
        super(data, TOTAL);
    }

    @Override
    public int nextIndex(WgbKey<K> key) {
        return key.compareTo(this.getKey()) > 0 ? RIGHT : LEFT;
    }

    public WhiteNode<K, T> getWhiteNode() {
        return (WhiteNode<K, T>) this.get(LEFT);
    }

    public BlackNode<K, T> getBlackNode() {
        return (BlackNode<K, T>) this.get(RIGHT);
    }

    public void setWhiteNode(WhiteNode<K, T> node) {
        this.setNode(LEFT, node);
    }

    public void setBlackNode(BlackNode<K, T> node) {
        this.setNode(RIGHT, node);
    }
}
