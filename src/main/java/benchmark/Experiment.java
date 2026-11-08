package benchmark;

import algorithms.EditDistance;
import algorithms.KmpSearch;
import algorithms.NaiveSearch;
import algorithms.RecursiveEditDistance;
import algorithms.StringSearch;
import algorithms.TableEditDistance;
import algorithms.TwoRowEditDistance;
import metrics.Metrics;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.IntSupplier;

/** A small repeatable experiment, not a replacement for a JVM microbenchmark framework. */
public class Experiment {
    private static volatile int answerSink;
    private static final int RUNS = 5;
    private static final int WARMUPS = 2;

    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("results"));
        InputGenerator generator = new InputGenerator();
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Path.of("results/results.csv")))) {
            out.println("algorithm,input,n,m,run,time_ms,work,comparisons,storage_ints,answer,seed");
            for (String type : new String[]{"random", "disjoint"}) {
                for (int n : new int[]{2, 4, 6, 8, 10}) {
                    String[] pair = generator.edit(type, n);
                    // The two DP versions also use these small inputs as a shared baseline.
                    runEdit(out, type, pair);
                }
                for (int n : new int[]{64, 128, 256, 512, 1024}) {
                    String[] pair = generator.edit(type, n);
                    EditDistance[] algorithms = {new TableEditDistance(), new TwoRowEditDistance()};
                    for (EditDistance algorithm : algorithms) measure(out, editName(algorithm), type, n, n,
                            () -> algorithm.distance(pair[0], pair[1]), algorithm.metrics());
                }
            }
            for (String type : new String[]{"random", "almost"}) {
                for (int n : new int[]{4096, 8192, 16384, 32768}) {
                    String[] pair = generator.search(type, n);
                    for (StringSearch algorithm : new StringSearch[]{new NaiveSearch(), new KmpSearch()}) {
                        measure(out, algorithm instanceof NaiveSearch ? "naive" : "kmp", type, n,
                                pair[1].length(), () -> algorithm.search(pair[0], pair[1]).size(), algorithm.metrics());
                    }
                }
            }
        }
        String environment = "Java: " + System.getProperty("java.version") + "\nOS: "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch")
                + "\nAvailable processors: " + Runtime.getRuntime().availableProcessors()
                + "\nSeed: " + InputGenerator.SEED + "\nWarmups: " + WARMUPS + "\nTimed runs: " + RUNS + "\n";
        Files.writeString(Path.of("results/environment.txt"), environment);
        PlotResults.main(new String[0]);
        System.out.println("Saved 330 measured runs and four PNG plots in results/.");
    }

    private static void runEdit(PrintWriter out, String type, String[] pair) {
        for (EditDistance algorithm : new EditDistance[]{new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance()}) {
            measure(out, editName(algorithm), type, pair[0].length(), pair[1].length(),
                    () -> algorithm.distance(pair[0], pair[1]), algorithm.metrics());
        }
    }

    private static String editName(EditDistance algorithm) {
        if (algorithm instanceof RecursiveEditDistance) return "recursive";
        return algorithm instanceof TableEditDistance ? "table" : "two-row";
    }

    private static void measure(PrintWriter out, String name, String type, int n, int m,
                                IntSupplier operation, Metrics metrics) {
        for (int i = 0; i < WARMUPS; i++) answerSink = operation.getAsInt();
        double[] times = new double[RUNS];
        for (int run = 1; run <= RUNS; run++) {
            long start = System.nanoTime();
            int answer = operation.getAsInt();
            long elapsed = System.nanoTime() - start;
            answerSink = answer;
            times[run - 1] = elapsed / 1_000_000.0;
            out.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%.6f,%d,%d,%d,%d,%d%n",
                    name, type, n, m, run, times[run - 1], metrics.work,
                    metrics.comparisons, metrics.storageInts, answer, InputGenerator.SEED);
        }
        Arrays.sort(times);
        System.out.printf(Locale.ROOT, "%s %-8s n=%5d median=%.6f ms%n", name, type, n, times[RUNS / 2]);
    }
}
