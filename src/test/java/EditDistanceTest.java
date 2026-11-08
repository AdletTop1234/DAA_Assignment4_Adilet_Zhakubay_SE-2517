import algorithms.EditDistance;
import algorithms.RecursiveEditDistance;
import algorithms.TableEditDistance;
import algorithms.TwoRowEditDistance;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditDistanceTest {
    private EditDistance[] distances() {
        return new EditDistance[]{new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance()};
    }

    @Test
    void referenceValuesAndEmptyStrings() {
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
    void hundredRandomPairsAgree() {
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

    private String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) result.append("abcd".charAt(random.nextInt(4)));
        return result.toString();
    }
}
