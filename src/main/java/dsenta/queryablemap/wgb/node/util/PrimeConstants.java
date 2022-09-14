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

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimeConstants {

    public static final Map<Integer, Integer> NEXT_PRIME = Map.ofEntries(
            Map.entry(2, 3),
            Map.entry(3, 5),
            Map.entry(5, 7),
            Map.entry(7, 11),
            Map.entry(11, 13),
            Map.entry(13, 17),
            Map.entry(17, 19),
            Map.entry(19, 23),
            Map.entry(23, 29),
            Map.entry(29, 31),
            Map.entry(31, 37),
            Map.entry(37, 41),
            Map.entry(43, 47),
            Map.entry(47, 53),
            Map.entry(53, 59),
            Map.entry(59, 61),
            Map.entry(61, 67),
            Map.entry(67, 71),
            Map.entry(71, 73),
            Map.entry(73, 79),
            Map.entry(79, 83),
            Map.entry(83, 89),
            Map.entry(89, 97),
            Map.entry(97, 101),
            Map.entry(101, 103),
            Map.entry(103, 107),
            Map.entry(107, 109),
            Map.entry(109, 113),
            Map.entry(113, 127),
            Map.entry(127, 131),
            Map.entry(131, 137),
            Map.entry(137, 139),
            Map.entry(139, 149),
            Map.entry(149, 151),
            Map.entry(151, 157),
            Map.entry(157, 163),
            Map.entry(163, 167),
            Map.entry(167, 173),
            Map.entry(173, 179),
            Map.entry(179, 181),
            Map.entry(181, 191),
            Map.entry(191, 193),
            Map.entry(193, 197),
            Map.entry(197, 199)
    );

    public static final int FIRST_PRIME = 2;
}