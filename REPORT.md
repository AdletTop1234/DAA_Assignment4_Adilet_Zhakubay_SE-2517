# Assignment 4: City Search & Spell-Check
Adilet Zhakubay, SE-2517

## Experiment Design Card (written before the runs)

| Field | Plan |
| --- | --- |
| Questions | Edit distance: how quickly does repeated recursion grow compared with DP, and does two-row DP save storage? Search: when does KMP avoid the comparisons repeated by naive search? |
| Sizes | Recursive distance: 2, 4, 6, 8, 10 (a 5x range, small enough for exponential work). Table and two rows: 64, 128, 256, 512, 1024 (16x range). Search text: 4096, 8192, 16384, 32768; pattern length n/16. Doubling sizes should make growth visible. |
| Inputs | Distance: independent random strings over abcd, and disjoint a^n / b^n to force three recursive branches at every non-base call. Search: random abcd text/pattern for early mismatches, and a^n with pattern a^(m-1)b for repeated almost-matches and no hits. |
| Metrics | Five timed runs after two warm-ups; save every run and use the median time in ms. Distance counts recursive calls or filled interior DP cells. Search counts character comparisons, including LPS construction for KMP. Also record allocated DP int slots to compare storage. |
| Prediction | Disjoint recursive distance will grow exponentially. DP cells will quadruple when n doubles, while two-row storage only doubles. Naive search will be near-linear on random input but quadratic on almost-matches because m grows with n. KMP should stay linear on both. |
| Plots | (1) Disjoint edit-distance work, logarithmic work axis; (2) DP storage, logarithmic slot axis; (3) search comparisons by input, logarithmic comparison axis; (4) median search time, logarithmic ms axis. Each plot answers the corresponding growth or storage question. |
| Reproduction | Java Random, seed 2517; generate each pair once before timing. The command reproduces inputs, answers and counters. Wall-clock times will vary between runs and machines. |

The plan above is committed before the first experiment. Results and discussion will be added after measurement.
