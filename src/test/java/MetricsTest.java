import algorithms.KmpSearch;
import algorithms.TableEditDistance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsTest {
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
}
