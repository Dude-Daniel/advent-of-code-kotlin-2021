package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListHelpersTest {

    @Test
    fun splitWhen() {
        val list = listOf("A", "B", "C", "D", "E")

        // matching
        assertEquals(
            expected = listOf(
                listOf("A", "B"),
                listOf("D", "E"),
            ),
            actual = list.splitWhen { it == "C" },
        )

        // no matches
        assertEquals(
            expected = listOf(listOf("A", "B", "C", "D", "E")),
            actual = list.splitWhen { it == "X" },
        )
    }

    @Test
    fun splitAtMatchingPositions() {
        val list = listOf(
            "123 456 789",
            "  A B    C ",
            "A A BB   CC",
        )

        // matching
        assertEquals(
            expected = listOf(
                listOf("123", "456", "789"),
                listOf("  A", "B  ", " C "),
                listOf("A A", "BB ", " CC"),
            ),
            actual = list.splitAtMatchingPositions { it == ' ' },
        )

        // no matches
        assertEquals(
            expected = listOf(
                listOf("123 456 789"),
                listOf("  A B    C "),
                listOf("A A BB   CC"),
            ),
            actual = list.splitAtMatchingPositions { it == 'X' },
        )
    }

    @Test
    fun indicesOfAll() {
        val list = listOf(
            "123 456 789",
            "  A B    C ",
            "A A BB   CC",
        )

        // matching
        assertEquals(
            expected = listOf(3, 7),
            actual = list.indicesOfAll { it == ' ' },
        )

        // no matches
        assertEquals(
            expected = emptyList(),
            actual = list.indicesOfAll { it == 'X' },
        )
    }

    @Test
    fun mapAll() {
        val lists = listOf(
            listOf("A", "B", "C"),
            listOf("D", "E", "F"),
        )

        assertEquals(
            expected = listOf(
                listOf("A1", "B1", "C1"),
                listOf("D1", "E1", "F1"),
            ),
            actual = lists.mapAll { it + 1 },
        )
    }

    @Test
    fun mapAllIndexed() {
        val lists = listOf(
            listOf("A", "B", "C"),
            listOf("D", "E", "F"),
        )

        assertEquals(
            expected = listOf(
                listOf("A 0x0", "B 1x0", "C 2x0"),
                listOf("D 0x1", "E 1x1", "F 2x1"),
            ),
            actual = lists.mapAllIndexed { point, string -> "$string ${point.x}x${point.y}" },
        )
    }

    @Test
    fun toMutableNestedList() {
        val lists = listOf(
            listOf("A", "B", "C"),
            listOf("D", "E", "F"),
        )

        assertEquals(
            expected = mutableListOf(
                mutableListOf("A", "B", "C"),
                mutableListOf("D", "E", "F"),
            ),
            actual = lists.toMutableNestedList(),
        )
    }

    @Test
    fun transpose() {
        val lists = listOf(
            listOf("A", "B", "C"),
            listOf("D", "E", "F"),
        )

        assertEquals(
            expected = listOf(
                listOf("A", "D"),
                listOf("B", "E"),
                listOf("C", "F"),
            ),
            actual = lists.transpose(),
        )
    }

    @Test
    fun duplicateEntry() {
        val lists = listOf(
            listOf("A1", "A2"),
            listOf("B1", "B2"),
            listOf("C1", "C2"),
        )

        // matches
        assertEquals(
            expected = listOf(
                listOf("A1", "A2"),
                listOf("B1", "B2"),
                listOf("B1", "B2"),
                listOf("C1", "C2"),
            ),
            actual = lists.duplicateEntry { it.contains("B1") },
        )

        // no matches
        assertEquals(
            expected = listOf(
                listOf("A1", "A2"),
                listOf("B1", "B2"),
                listOf("C1", "C2"),
            ),
            actual = lists.duplicateEntry { it.contains("X") },
        )
    }
}
