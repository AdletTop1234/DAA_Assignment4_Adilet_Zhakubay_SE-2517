package org.example;

public interface EditDistance {
    int distance(String a, String b);
    Metrics metrics();
}
