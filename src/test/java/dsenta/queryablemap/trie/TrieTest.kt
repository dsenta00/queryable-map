package dsenta.queryablemap.trie

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TrieTest {

    @Test
    fun insert() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Mate", "Petar", "Ante", "Tomislav")

        // When
        inputData.forEach { trie[it] = it }

        // Then
        assertThat(trie)
                .hasSize(inputData.size)
                .let {
                    inputData.forEach { data -> it.containsKey(data) }
                }
    }

    @Test
    fun delete() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Matea", "Petar", "Mile", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // When remove Iva
        val deleted = arrayListOf<String>()

        inputData.forEach {
            deleted += it

            // When
            trie.remove(it)

            // Then
            assertThat(trie)
                    .hasSize(inputData.size - deleted.size)
                    .let {
                        inputData.filter { name -> !deleted.contains(name) }.forEach { data -> it.containsKey(data) }
                    }
        }
    }

    @Test
    fun delete2() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("junit", "junit2")

        inputData.forEach { trie[it] = it }

        // When
        trie.remove("junit2")

        // Then
        assertThat(trie)
                .hasSize(1)
                .doesNotContainKey("junit2")
                .containsKey("junit")
    }

    @Test
    fun deleteEmpty() {
        // Given
        val trie = Trie<String, String>()

        // When
        val removed = trie.remove("junit2")

        // Then
        assertThat(removed).isNull();
        assertThat(trie).isEmpty()
    }

    @Test
    fun deleteEmptyNull() {
        // Given
        val trie = Trie<String, String>()

        // When
        val removed = trie.remove(null)

        // Then
        assertThat(removed).isNull();
        assertThat(trie).isEmpty()
    }

    @Test
    fun clear() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // When
        trie.clear()
        // Then
        assertThat(trie).isEmpty()
    }

    @Test
    fun get() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.asc.map { it.key })
                .hasSize(inputData.size)
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.desc.map { it.key })
                .hasSize(inputData.size)
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")
    }

    @Test
    fun getBiggerThan() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.getBiggerThanAsc("A").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("A").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Ante").map { it.key })
                .containsExactly("Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Ante").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("I").map { it.key })
                .containsExactly("Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("I").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Iva").map { it.key })
                .containsExactly("Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Iva").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Mi").map { it.key })
                .containsExactly("Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Mi").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Mil").map { it.key })
                .containsExactly("Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Mil").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Miljenk").map { it.key })
                .containsExactly("Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Miljenk").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Miljenka").map { it.key })
                .containsExactly("Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Miljenka").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko")

        // Then ASC
        assertThat(trie.getBiggerThanAsc("Z").map { it.key }).isEmpty()

        // Then DESC
        assertThat(trie.getBiggerThanDesc("Z").map { it.key }).isEmpty()
    }

    @Test
    fun getBiggerThanEquals() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("A").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("A").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Ante").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Ante").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("I").map { it.key })
                .containsExactly("Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("I").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Iva").map { it.key })
                .containsExactly("Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Iva").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Mi").map { it.key })
                .containsExactly("Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Mi").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Mil").map { it.key })
                .containsExactly("Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Mil").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Miljenk").map { it.key })
                .containsExactly("Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Miljenk").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Miljenka").map { it.key })
                .containsExactly("Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Miljenka").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka")

        // Then ASC
        assertThat(trie.getBiggerThanEqualsAsc("Z").map { it.key }).isEmpty()

        // Then DESC
        assertThat(trie.getBiggerThanEqualsDesc("Z").map { it.key }).isEmpty()
    }

    @Test
    fun getLessThan() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.getLessThanAsc("Z").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getLessThanDesc("Z").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanAsc("Tomislav").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra")

        // Then DESC
        assertThat(trie.getLessThanDesc("Tomislav").map { it.key })
                .containsExactly("Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanAsc("T").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra")

        // Then DESC
        assertThat(trie.getLessThanDesc("T").map { it.key })
                .containsExactly("Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanAsc("P").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko")

        // Then DESC
        assertThat(trie.getLessThanDesc("P").map { it.key })
                .containsExactly("Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanAsc("Mi").map { it.key })
                .containsExactly("Ante", "Iva", "Mate")

        // Then DESC
        assertThat(trie.getLessThanDesc("Mi").map { it.key })
                .containsExactly("Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanAsc("A").map { it.key }).isEmpty()

        // Then DESC
        assertThat(trie.getLessThanDesc("A").map { it.key }).isEmpty()
    }

    @Test
    fun getLessThanEquals() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.getLessThanEqualsAsc("Z").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getLessThanEqualsDesc("Z").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanEqualsAsc("Tomislav").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getLessThanEqualsDesc("Tomislav").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanEqualsAsc("T").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra")

        // Then DESC
        assertThat(trie.getLessThanEqualsDesc("T").map { it.key })
                .containsExactly("Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanEqualsAsc("P").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko")

        // Then DESC
        assertThat(trie.getLessThanEqualsDesc("P").map { it.key })
                .containsExactly("Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanEqualsAsc("Mi").map { it.key })
                .containsExactly("Ante", "Iva", "Mate")

        // Then DESC
        assertThat(trie.getLessThanEqualsDesc("Mi").map { it.key })
                .containsExactly("Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getLessThanEqualsAsc("A").map { it.key }).isEmpty()

        // Then DESC
        assertThat(trie.getLessThanEqualsDesc("A").map { it.key }).isEmpty()
    }

    @Test
    fun getBetweenEquals() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.getBetweenAsc("A", "Z").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBetweenDesc("A", "Z").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getBetweenAsc("Ante", "Tomislav").map { it.key })
                .containsExactly("Ante", "Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getBetweenDesc("Ante", "Tomislav").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva", "Ante")

        // Then ASC
        assertThat(trie.getBetweenAsc("M", "N").map { it.key })
                .containsExactly("Mate", "Mijo", "Miljenka", "Miljenko")

        // Then DESC
        assertThat(trie.getBetweenDesc("M", "N").map { it.key })
                .containsExactly("Miljenko", "Miljenka", "Mijo", "Mate")

        // Then ASC
        assertThat(trie.getBetweenAsc("Ma", "Mijo").map { it.key })
                .containsExactly("Mate", "Mijo")

        // Then DESC
        assertThat(trie.getBetweenDesc("Ma", "Mijo").map { it.key })
                .containsExactly("Mijo", "Mate")
    }

    @Test
    fun getNotEquals() {
        // Given
        val trie = Trie<String, String>()
        val inputData = listOf("Iva", "Mijo", "Miljenko", "Miljenka", "Mate", "Petar", "Petra", "Ante", "Tomislav")

        inputData.forEach { trie[it] = it }

        // Then ASC
        assertThat(trie.getNotEqualsAsc("Ante").map { it.key })
                .containsExactly("Iva", "Mate", "Mijo", "Miljenka", "Miljenko", "Petar", "Petra", "Tomislav")

        // Then DESC
        assertThat(trie.getNotEqualsDesc("Ante").map { it.key })
                .containsExactly("Tomislav", "Petra", "Petar", "Miljenko", "Miljenka", "Mijo", "Mate", "Iva")
    }
}