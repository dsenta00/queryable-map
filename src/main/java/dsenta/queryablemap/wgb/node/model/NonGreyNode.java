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

import dsenta.queryablemap.wgb.node.util.Mod;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NonGreyNode<K extends Comparable<K>, T> extends WgbNode<K, T> implements Serializable {
    private static final long serialVersionUID = -6079875398947273613L;

    public NonGreyNode(WgbData<K, T> data, int capacity) {
        super(data, capacity);
    }

    @Override
    public GreyNode<K, T> get(int index) {
        return (GreyNode<K, T>) super.get(index);
    }

    public GreyNode<K, T> next(WgbKey<K> key) {
        return this.get(nextIndex(key));
    }

    @Override
    public int nextIndex(WgbKey<K> key) {
        return Mod.fastMod(Math.abs(key.hashCode()), this.getCapacity());
    }
}