package org.example;

/** Counters belong to one algorithm object and are reset for each call. */
public class Metrics {
    public long work;
    public long comparisons;
    public long storageInts;

    public void reset() {
        work = 0;
        comparisons = 0;
        storageInts = 0;
    }
}
