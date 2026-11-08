package org.example;

import algorithms.EditDistance;
import algorithms.KmpSearch;
import algorithms.NaiveSearch;
import algorithms.RecursiveEditDistance;
import algorithms.StringSearch;
import algorithms.TableEditDistance;
import algorithms.TwoRowEditDistance;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    private EditDistance[] distances() {
        return new EditDistance[]{new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance()};
    }

    private StringSearch[] searches() {
        return new StringSearch[]{new NaiveSearch(), new KmpSearch()};
    }

    @Test
    void editReferenceValuesAndEmptyStrings() {
        for (EditDistance algorithm : distances()) {
            assertEquals(3, algorithm.distance("kitten", "sitting"));
            assertEquals(2, algorithm.distance("flaw", "lawn"));
            assertEquals(5, algorithm.distance("intention", "execution"));
            assertEquals(3, algorithm.distance("", "abc"));
            assertEquals(3, algorithm.distance("abc", ""));
            assertEquals(0, algorithm.distance("", ""));
            assertEquals(0, algorithm.distance("abc", "abc"));
            assertEquals(2, algorithm.distance("Abylai Han", "Abylai Khan")); // Case-sensitive: H -> K, insert h.
        }
    }

    @Test
    void hundredRandomEditPairsAgree() {
        Random random = new Random(2517);
        EditDistance[] algorithms = distances();
        for (int i = 0; i < 100; i++) {
            String a = randomString(random, random.nextInt(7));
            String b = randomString(random, random.nextInt(7));
            int expected = algorithms[0].distance(a, b);
            for (EditDistance algorithm : algorithms) {
                assertEquals(expected, algorithm.distance(a, b), a + " / " + b);
                assertEquals(expected, algorithm.distance(b, a), "symmetry");
            }
        }
    }

    @Test
    void lpsReferenceArrays() {
        KmpSearch kmp = new KmpSearch();
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, kmp.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, kmp.lps("ABABCABAB"));
        assertArrayEquals(new int[0], kmp.lps(""));
        assertArrayEquals(new int[]{0}, kmp.lps("A"));
    }

    @Test
    void searchReferencesAndOverlaps() {
        for (StringSearch algorithm : searches()) {
            assertEquals(List.of(10), algorithm.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), algorithm.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), algorithm.search("ABABABA", "ABA"));
            assertEquals(List.of(0), algorithm.search("abc", "abc"));
            assertEquals(List.of(), algorithm.search("abc", "d"));
        }
    }

    @Test
    void searchEdgeCases() {
        for (StringSearch algorithm : searches()) {
            assertThrows(IllegalArgumentException.class, () -> algorithm.search("abc", ""));
            assertThrows(IllegalArgumentException.class, () -> algorithm.search("", ""));
            assertEquals(List.of(), algorithm.search("a", "abc"));
            assertEquals(List.of(), algorithm.search("", "a"));
        }
    }

    @Test
    void hundredRandomSearchesAgreeWithIndependentOracle() {
        Random random = new Random(2517);
        for (int i = 0; i < 100; i++) {
            String text = randomString(random, random.nextInt(101));
            String pattern = randomString(random, 1 + random.nextInt(12));
            List<Integer> naive = new NaiveSearch().search(text, pattern);
            assertEquals(naive, new KmpSearch().search(text, pattern));
            List<Integer> expected = new java.util.ArrayList<>();
            for (int at = text.indexOf(pattern); at >= 0; at = text.indexOf(pattern, at + 1)) expected.add(at);
            assertEquals(expected, naive);
        }
    }

    @Test
    void countersResetAndIncludeKmpPreprocessing() {
        TableEditDistance table = new TableEditDistance();
        table.distance("abc", "xyz");
        table.distance("a", "b");
        assertEquals(1, table.metrics().work);
        assertEquals(4, table.metrics().storageInts);
        KmpSearch kmp = new KmpSearch();
        kmp.search("aaaaa", "aa");
        assertEquals(6, kmp.metrics().comparisons); // 1 LPS + 5 search comparisons.
        kmp.search("x", "y");
        assertEquals(1, kmp.metrics().comparisons);
    }

    private String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) result.append("abcd".charAt(random.nextInt(4)));
        return result.toString();
    }
}
