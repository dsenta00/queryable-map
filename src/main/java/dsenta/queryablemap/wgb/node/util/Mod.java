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

import dsenta.queryablemap.exception.DivideByZeroException;

public final class Mod {

    public static int fastMod(int a, int b) {
        switch (b) {
            case 0:
                throw new DivideByZeroException();
            case 1:
                return 0;
            case 2:
                return a & 1;
            case 3:
                int x = a;
                x = (x >> 16) + (x & 0xffff);
                x = (x >> 10) + (x & 0x3ff);
                x = (x >> 6) + (x & 0x3f);
                x = (x >> 4) + (x & 0xf);
                x = (x >> 2) + (x & 0x3);
                x = (x >> 2) + (x & 0x3);
                x = (x >> 2) + (x & 0x3);
                if (x == 3) x = 0;
                return x;
            case 5:
                while (a > 9) {
                    int s = 0; /* accumulator for the sum of the digits */
                    while (a != 0) {
                        s = s + (a & 7);
                        a = (a >> 3) * 3;
                    }
                    a = s;
                }
                /* note, at this point: a < 10 */
                if (a > 4) a = a - 5;
                return a;

            case 7:
                a = (a >> 24) + (a & 0xFFFFFF); /* sum base 2**24 digits
                                        a <= 0x10001FE worst case 0xFFFFFE*/
                a = (a >> 12) + (a & 0xFFF);    /* sum base 2**12 digits
                                        a <= 0x1FFD */
                a = (a >>  6) + (a & 0x3F);     /* sum base 2**6 digits
                                        a <= 0xBC; worst case ? */
                a = (a >>  3) + (a & 0x7);      /* sum base 2**2 digits
                                        a <= 0x1B; worst case ? */
                a = (a >>  2) + (a & 0x7);      /* sum base 2**2 digits
                                        a <= 0x6; worst case ? */
                if (a > 5) a = a - 6;
                return a;
            default:
                return (a - (a / b) * b);
        }
    }
}
