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
    fun combinations() {
        // empty list - size 2
        assertEquals(
            expected = emptyList(),
            actual = emptyList<String>().combinations(size = 2),
        )

        // one element - size 2
        assertEquals(
            expected = emptyList(),
            actual = listOf("A").combinations(size = 2),
        )

        // two elements - size 2
        assertEquals(
            expected = listOf(
                listOf("A", "B"),
            ),
            actual = listOf("A", "B").combinations(size = 2),
        )

        // three elements - size 2
        assertEquals(
            expected = listOf(
                listOf("A", "B"),
                listOf("A", "C"),
                listOf("B", "C"),
            ),
            actual = listOf("A", "B", "C").combinations(size = 2),
        )

        // three elements - size 3
        assertEquals(
            expected = listOf(
                listOf("A", "B", "C"),
            ),
            actual = listOf("A", "B", "C").combinations(size = 3),
        )

        // five elements - size 2
        assertEquals(
            expected = listOf(
                listOf("A", "B"),
                listOf("A", "C"),
                listOf("A", "D"),
                listOf("A", "E"),

                listOf("B", "C"),
                listOf("B", "D"),
                listOf("B", "E"),

                listOf("C", "D"),
                listOf("C", "E"),

                listOf("D", "E"),
            ),
            actual = listOf("A", "B", "C", "D", "E").combinations(size = 2),
        )

        // five elements - size 3
        assertEquals(
            expected = listOf(
                listOf("A", "B", "C"),
                listOf("A", "B", "D"),
                listOf("A", "B", "E"),
                listOf("A", "C", "D"),
                listOf("A", "C", "E"),
                listOf("A", "D", "E"),

                listOf("B", "C", "D"),
                listOf("B", "C", "E"),
                listOf("B", "D", "E"),

                listOf("C", "D", "E"),
            ),
            actual = listOf("A", "B", "C", "D", "E").combinations(size = 3),
        )

        // five elements - size 3
        assertEquals(
            expected = listOf(
                listOf("A", "B", "C", "D"),
                listOf("A", "B", "C", "E"),
                listOf("A", "B", "D", "E"),
                listOf("A", "C", "D", "E"),
                listOf("B", "C", "D", "E"),
            ),
            actual = listOf("A", "B", "C", "D", "E").combinations(size = 4),
        )
    }

    @Test
    fun permutations() {
        // empty list
        assertEquals(
            expected = emptyList(),
            actual = emptyList<String>().permutations(),
        )

        // one element
        assertEquals(
            expected = listOf(
                listOf("A"),
            ),
            actual = listOf("A").permutations(),
        )

        // two elements
        assertEquals(
            expected = listOf(
                listOf("A", "B"),
                listOf("B", "A"),
            ),
            actual = listOf("A", "B").permutations(),
        )

        // three elements
        assertEquals(
            expected = listOf(
                listOf("A", "B", "C"),
                listOf("A", "C", "B"),
                listOf("B", "A", "C"),
                listOf("B", "C", "A"),
                listOf("C", "B", "A"),
                listOf("C", "A", "B"),
            ),
            actual = listOf("A", "B", "C").permutations(),
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

    @Test
    fun swap() {
        val list = mutableListOf(1, 2, 3, 4, 5)

        list.swap(1, 3)

        assertEquals(
            expected = mutableListOf(1, 4, 3, 2, 5),
            actual = list,
        )
    }
}
