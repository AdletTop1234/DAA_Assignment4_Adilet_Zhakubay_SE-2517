package algorithms;

import metrics.Metrics;

public interface EditDistance {
    int distance(String a, String b);
    Metrics metrics();
}
