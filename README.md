# DAA Assignment 4 - DP and String Algorithms

Adilet Zhakubay, SE-2517

City Search & Spell-Check: three Levenshtein distance implementations and two exact string searches. The code uses ordinary Java loops, arrays and lists.

Repository: https://github.com/AdletTop1234/DAA_Assignment4_Adilet_Zhakubay_SE-2517/tree/main

Submission branch: `main`. Assignment release: `v1.0`. Structured project update: `v1.1`.

## Build and test

Requirements: JDK 17 or newer and Maven 3.9 or newer. Set `JAVA_HOME` to the JDK and put Maven on PATH. The first Maven build downloads JUnit 5 and build plugins.

```shell
mvn clean verify
```

On this Windows machine, the helper also finds Maven bundled with IntelliJ and a JDK in `~/.jdks`:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1 test
```

Seven JUnit 5 tests cover all assignment reference values, empty strings, overlaps, empty-pattern exceptions, longer patterns, the two LPS arrays, counter reset, 100 random edit-distance pairs and 100 random search pairs. Search is also checked against Java's `indexOf` as an independent oracle.

## Run the experiment

After building:

```shell
java -cp target/classes benchmark.Experiment
```

Or build and run in one Windows command:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1 experiment
```

This overwrites `results/results.csv`, `results/environment.txt` and four PNG plots. It uses seed 2517, two warm-ups and five measured runs for every case. Inputs, answers and operation counters repeat exactly; elapsed times vary. Re-running the experiment does not rewrite the report's recorded times. To redraw plots from an existing CSV without measuring again:

```shell
java -cp target/classes benchmark.PlotResults
```

## Project structure

```text
src/
  main/java/
    algorithms/
      EditDistance.java
      RecursiveEditDistance.java
      TableEditDistance.java
      TwoRowEditDistance.java
      StringSearch.java
      NaiveSearch.java
      KmpSearch.java
    benchmark/
      InputGenerator.java
      Experiment.java
      PlotResults.java
    metrics/
      Metrics.java
  test/java/
    EditDistanceTest.java
    LpsTest.java
    StringSearchTest.java
    MetricsTest.java
results/
  results.csv
  environment.txt
  plots/
    edit-work.png
    edit-storage.png
    search-comparisons.png
    search-time.png
pom.xml
run.ps1
README.md
REPORT.md
```

- `src/main/java/algorithms/`: edit-distance and string-search interfaces and implementations.
- `src/main/java/benchmark/`: input generator, experiment and PNG plotting.
- `src/main/java/metrics/`: operation and storage counters.
- `src/test/java/`: separate JUnit 5 classes for edit distance, LPS, string search and metrics.
- `results/results.csv`: one row per timed invocation, 330 rows total.
- `results/plots/`: four charts generated directly from the CSV.
- `REPORT.md`: design card committed before measurement, results and answers.

Both interfaces expose the same operation within their family. Strings are case-sensitive Java UTF-16 sequences; positions are zero-based UTF-16 indices. Search returns all occurrences including overlaps. An empty pattern throws `IllegalArgumentException`; a longer pattern returns an empty list. Inputs are expected to be non-null. Algorithm instances keep their last call's counters and are intended for one thread.

CSV columns: `algorithm,input,n,m,run,time_ms,work,comparisons,storage_ints,answer,seed`. For edit distance, `work` counts recursive calls (including base cases) or interior DP cells; `answer` is the distance. For search, `comparisons` counts character comparisons, including KMP preprocessing, and `answer` is the number of matches. `storage_ints` counts allocated algorithm array slots, excluding output lists and JVM overhead; zero for recursion does not mean zero stack memory. The charts aggregate the five runs by median.

## Git branches

- `main`: completed project with the merged changes.
- `refactor/project-structure`: source packages and updated entry points.
- `test/separate-suites`: four focused test classes.
- `docs/project-layout`: project tree, commands and branch documentation.

Each work branch has its own commit and is merged into `main` with a merge commit. To see the branch graph:

```shell
git log --graph --decorate --oneline --all
```

Tag `v1.0` records the original assignment release. Tag `v1.1` records the reorganized source and tests. The CSV and report contain the original measured results; changing package names does not change the experiment design.

## Submission

The submission ZIP is `submission/DAA_Assignment4_Adilet_Zhakubay_SE-2517.zip`. It contains the source, tests, report, results and this README. Upload it to Moodle and include the repository link above. Git history and tag are in the repository, not inside the ZIP.

## AI-use acknowledgement

I used OpenAI Codex to help implement the algorithms and tests, plan the experiment, generate plots, and draft the report and documentation. The submitted measurements were produced by running the included Java experiment. This acknowledgement does not claim that the work was completed without AI assistance.
