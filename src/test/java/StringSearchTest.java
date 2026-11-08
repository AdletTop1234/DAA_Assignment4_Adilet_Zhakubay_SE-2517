import algorithms.KmpSearch;
import algorithms.NaiveSearch;
import algorithms.StringSearch;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringSearchTest {
    private StringSearch[] searches() {
        return new StringSearch[]{new NaiveSearch(), new KmpSearch()};
    }

    @Test
    void referenceMatchesAndOverlaps() {
        for (StringSearch algorithm : searches()) {
            assertEquals(List.of(10), algorithm.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), algorithm.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), algorithm.search("ABABABA", "ABA"));
            assertEquals(List.of(0), algorithm.search("abc", "abc"));
            assertEquals(List.of(), algorithm.search("abc", "d"));
        }
    }

    @Test
    void edgeCases() {
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
            List<Integer> expected = new ArrayList<>();
            for (int at = text.indexOf(pattern); at >= 0; at = text.indexOf(pattern, at + 1)) expected.add(at);
            assertEquals(expected, naive);
        }
    }

    private String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) result.append("abcd".charAt(random.nextInt(4)));
        return result.toString();
    }
}
