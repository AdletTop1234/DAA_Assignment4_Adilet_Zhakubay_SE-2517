package algorithms;

import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch implements StringSearch {
    private final Metrics metrics = new Metrics();

    /** lps[i] is the longest proper prefix which is also a suffix of p[0..i]. */
    public int[] lps(String p) {
        metrics.reset();
        return buildLps(p);
    }

    private int[] buildLps(String p) {
        int[] lps = new int[p.length()];
        metrics.storageInts += lps.length;
        int length = 0;
        int i = 1;
        while (i < p.length()) {
            metrics.comparisons++;
            if (p.charAt(i) == p.charAt(length)) {
                lps[i++] = ++length;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern) {
        metrics.reset();
        if (pattern.isEmpty()) throw new IllegalArgumentException("Pattern must not be empty");
        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) return matches;
        int[] lps = buildLps(pattern);
        int i = 0;
        int j = 0;
        while (i < text.length()) {
            metrics.comparisons++; // Includes successful and failed comparisons.
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = lps[j - 1]; // Keep the matching suffix to allow overlap.
                }
            } else if (j > 0) {
                j = lps[j - 1]; // i never moves backwards.
            } else {
                i++;
            }
        }
        return matches;
    }

    @Override
    public Metrics metrics() { return metrics; }
}
