package dsenta.queryablemap.wgb

import dsenta.queryablemap.testutil.RandomGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Collectors
import kotlin.math.pow

internal class WhiteGreyBlackTreeTest {

    @ParameterizedTest
    @ValueSource(ints = [100, 1_000, 100_000, 1_000_000, 5_000_000])
    fun testTree(numbers: Int) {
        val tree = WhiteGreyBlackTree<Int, Int>()
        val rand = RandomGenerator.streamRandomNumbers(numbers).collect(Collectors.toList())

        rand.forEachIndexed { i, el ->
            tree[el] = el

            val expectedDepth = (minDepth(i) + 1) * 1.5
            assertTrue(tree.depth() <= expectedDepth, "Tree depth ${tree.depth()}, expected depth $expectedDepth, on number of elements ${i + 1}")
        }
    }

    @Test
    fun test_get_int() {
        val tree = WhiteGreyBlackTree<Int, Int>()

        tree[1] = 1
        tree[2] = 2
        tree[3] = 3
        tree[4] = 4
        tree[5] = 5
        tree[6] = 6
        tree[7] = 7
        tree[8] = 8
        assertEquals(
                listOf(1, 2, 3, 4, 5, 6, 7, 8),
                tree.asc.map { it.value }
        )
        assertEquals(
                listOf(8, 7, 6, 5, 4, 3, 2, 1),
                tree.desc.map { it.value }
        )
        assertEquals(
                listOf(2, 3, 4, 5, 6, 7),
                tree.getBetweenAsc(2, 7).map { it.value }
        )
        assertEquals(
                listOf(7, 6, 5, 4, 3, 2),
                tree.getBetweenDesc(2, 7).map { it.value }
        )
        assertEquals(
                listOf(3, 4, 5, 6, 7, 8),
                tree.getBiggerThanAsc(2).map { it.value }
        )
        assertEquals(
                listOf(8, 7, 6, 5, 4, 3),
                tree.getBiggerThanDesc(2).map { it.value }
        )
        assertEquals(
                listOf(8, 7, 6, 5, 4, 3, 2),
                tree.getBiggerThanEqualsDesc(2).map { it.value }
        )
        assertEquals(
                listOf(2, 3, 4, 5, 6, 7, 8),
                tree.getBiggerThanEqualsAsc(2).map { it.value }
        )
        assertEquals(
                listOf(3, 2, 1),
                tree.getLessThanEqualsDesc(3).map { it.value }
        )
        assertEquals(
                listOf(1, 2, 3, 4),
                tree.getLessThanEqualsAsc(4).map { it.value }
        )
        assertEquals(
                listOf(2, 1),
                tree.getLessThanDesc(3).map { it.value }
        )
        assertEquals(
                listOf(1, 2),
                tree.getLessThanAsc(3).map { it.value }
        )
        assertEquals(
                listOf(1, 2, 3, 4, 6, 7, 8),
                tree.getNotEqualsAsc(5).map { it.value }
        )
        assertEquals(
                listOf(8, 7, 6, 4, 3, 2, 1),
                tree.getNotEqualsDesc(5).map { it.value }
        )
    }

    @Test
    fun test_get_int_on_delete() {
        val tree = WhiteGreyBlackTree<Int, Int>()
        tree[0] = 9
        tree[1] = 8
        tree[2] = 7
        tree[3] = 6
        tree[4] = 5
        tree[5] = 4
        tree[6] = 3
        tree[7] = 2
        tree[8] = 1
        tree[9] = 0
        tree.remove(0)
        tree.remove(9)
        tree.remove(5)
        tree[5] = 4
        assertEquals(
                listOf(8, 7, 6, 5, 4, 3, 2, 1),
                tree.asc.map { it.value }
        )
        assertEquals(
                listOf(1, 2, 3, 4, 5, 6, 7, 8),
                tree.asc.map { it.key }
        )
        assertEquals(
                listOf(1, 2, 3, 4, 5, 6, 7, 8),
                tree.desc.map { it.value }
        )
        assertEquals(
                listOf(8, 7, 6, 5, 4, 3, 2, 1),
                tree.desc.map { it.key }
        )
        assertEquals(
                listOf(7, 6, 5, 4, 3, 2),
                tree.getBetweenAsc(2, 7).map { it.value }
        )
        assertEquals(
                listOf(2, 3, 4, 5, 6, 7),
                tree.getBetweenDesc(2, 7).map { it.value }
        )
        assertEquals(
                listOf(1, 2, 3, 4, 5, 6),
                tree.getBiggerThanDesc(2).map { it.value }
        )
        assertEquals(
                listOf(6, 5, 4, 3, 2, 1),
                tree.getBiggerThanAsc(2).map { it.value }
        )
        assertEquals(
                listOf(1, 2, 3, 4, 5, 6, 7),
                tree.getBiggerThanEqualsDesc(2).map { it.value }
        )
        assertEquals(
                listOf(7, 6, 5, 4, 3, 2, 1),
                tree.getBiggerThanEqualsAsc(2).map { it.value }
        )
        assertEquals(
                listOf(6, 7, 8),
                tree.getLessThanEqualsDesc(3).map { it.value }
        )
        assertEquals(
                listOf(8, 7, 6, 5),
                tree.getLessThanEqualsAsc(4).map { it.value }
        )
        assertEquals(
                listOf(7, 8),
                tree.getLessThanDesc(3).map { it.value }
        )
        assertEquals(
                listOf(8, 7),
                tree.getLessThanAsc(3).map { it.value }
        )
        tree.remove(5)
        assertEquals(
                listOf(1, 2, 3, 4, 6, 7, 8),
                tree.asc.map { it.key }
        )
        tree.remove(8)
        assertEquals(
                listOf(1, 2, 3, 4, 6, 7),
                tree.asc.map { it.key }
        )
        tree.remove(1)
        assertEquals(
                listOf(2, 3, 4, 6, 7),
                tree.asc.map { it.key }
        )
        tree.remove(3)
        assertEquals(
                listOf(2, 4, 6, 7),
                tree.asc.map { it.key }
        )
        tree.remove(6)
        assertEquals(
                listOf(2, 4, 7),
                tree.asc.map { it.key }
        )
        tree.remove(7)
        assertEquals(
                listOf(2, 4),
                tree.asc.map { it.key }
        )
        tree.remove(2)
        assertEquals(
                listOf(4),
                tree.asc.map { it.key }
        )
        tree.remove(4)
        assertEquals(
                listOf<Any>(),
                tree.asc.map { it.key }
        )
    }

    private fun minDepth(n: Int): Int {
        (1..n).forEach { i ->
            if (i.toDouble().pow(i / 2) > n) {
                return i - 1;
            }
        }

        return n
    }
}