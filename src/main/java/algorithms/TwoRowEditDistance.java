package algorithms;

import metrics.Metrics;

public class TwoRowEditDistance implements EditDistance {
    private final Metrics metrics = new Metrics();

    // State: previous[j]=D(i-1,j), current[j]=D(i,j) for prefix distances.
    // Transition: min(previous[j]+1, current[j-1]+1, previous[j-1]+mismatch).
    // Base: previous[j]=j; current[0]=i for each row, then swap the rows.
    @Override
    public int distance(String a, String b) {
        metrics.reset();
        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];
        metrics.storageInts += previous.length;
        metrics.storageInts += current.length;
        for (int j = 0; j <= b.length(); j++) previous[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                metrics.work++;
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(previous[j - 1] + cost,
                        Math.min(previous[j] + 1, current[j - 1] + 1));
            }
            int[] temp = previous;
            previous = current;
            current = temp;
        }
        return previous[b.length()];
    }

    @Override
    public Metrics metrics() { return metrics; }
}
