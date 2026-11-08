package org.example;

import java.util.Random;

public class InputGenerator {
    public static final long SEED = 2517;
    private final Random random = new Random(SEED);

    public String randomString(int size) {
        StringBuilder text = new StringBuilder(size);
        for (int i = 0; i < size; i++) text.append("abcd".charAt(random.nextInt(4)));
        return text.toString();
    }

    public String[] edit(String type, int n) {
        if (type.equals("disjoint")) return new String[]{"a".repeat(n), "b".repeat(n)};
        return new String[]{randomString(n), randomString(n)};
    }

    public String[] search(String type, int n) {
        int m = n / 16;
        if (type.equals("almost")) return new String[]{"a".repeat(n), "a".repeat(m - 1) + "b"};
        return new String[]{randomString(n), randomString(m)};
    }
}
