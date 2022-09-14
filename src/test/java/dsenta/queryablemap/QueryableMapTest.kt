package dsenta.queryablemap

import dsenta.queryablemap.statistics.TreeStatisticsByTreeName
import dsenta.queryablemap.trie.Trie
import dsenta.queryablemap.wgb.WhiteGreyBlackTree
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant

internal class QueryableMapTest {

    @ParameterizedTest
    @MethodSource("outputResult_source")
    @Disabled
    fun outputResult(tree: QueryableMap<String, String>) {
        TreeStatisticsByTreeName().set(1_000_000).calculateTree(tree)
    }

    companion object {
        @JvmStatic
        private fun outputResult_source(): List<QueryableMap<String, String>> {
            return listOf(
                WhiteGreyBlackTree(),
                Trie()
            )
        }
    }

    @Test
    @Disabled
    fun trieFasterToFetchThanWgbTrie() {
        // Given
        val trie = Trie<String, String>()
        val wgb = WhiteGreyBlackTree<String, String>()
        val inputData =
            listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Matea", "Petar", "Mile", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }
        inputData.forEach { wgb[it] = it }

        // When
        val trieMs = nowMs().let { startMs ->
            repeat(1000000) {
                inputData.forEach { data -> trie[data] }
            }
            nowMs() - startMs
        }

        val wgbMs = nowMs().let { startMs ->
            repeat(1000000) {
                inputData.forEach { data -> wgb[data] }
            }
            nowMs() - startMs
        }

        // Then
        assertThat(trieMs).isLessThan(wgbMs)
    }

    private fun nowMs(): Long = Instant.now().toEpochMilli()
}