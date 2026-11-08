package org.example;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/** Dependency-free line charts generated from the saved CSV, not theoretical formulas. */
public class PlotResults {
    private record Row(String algorithm, String input, int n, double time, long work,
                       long comparisons, long storage) { }
    private record Point(int n, double value) { }

    public static void main(String[] args) throws Exception {
        List<Row> rows = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of("results/results.csv")).subList(1,
                Files.readAllLines(Path.of("results/results.csv")).size())) {
            String[] c = line.split(",");
            rows.add(new Row(c[0], c[1], Integer.parseInt(c[2]), Double.parseDouble(c[5]),
                    Long.parseLong(c[6]), Long.parseLong(c[7]), Long.parseLong(c[8])));
        }
        Files.createDirectories(Path.of("results/plots"));
        chart(rows, "edit-work.png", "Edit distance: repeated work on disjoint strings",
                "String length n (characters)", "Calls / cells (count, log10 scale)",
                r -> r.input.equals("disjoint") && r.n <= 10, r -> r.work, false);
        chart(rows, "edit-storage.png", "DP storage: full table versus two rows",
                "String length n (characters)", "Allocated int slots (count, log10 scale)",
                r -> r.input.equals("disjoint") && r.n >= 64 && r.storage > 0, r -> r.storage, false);
        chart(rows, "search-comparisons.png", "Search comparisons, including KMP preprocessing",
                "Text length n (characters), pattern length n/16", "Character comparisons (count, log10 scale)",
                r -> r.algorithm.equals("naive") || r.algorithm.equals("kmp"), r -> r.comparisons, true);
        chart(rows, "search-time.png", "Search: median of five timed runs",
                "Text length n (characters), pattern length n/16", "Median elapsed time (ms, log10 scale)",
                r -> r.algorithm.equals("naive") || r.algorithm.equals("kmp"), r -> r.time, true);
    }

    private static void chart(List<Row> rows, String filename, String title, String xLabel,
                              String yLabel, Predicate<Row> include, ToDoubleFunction<Row> metric,
                              boolean showInput) throws Exception {
        Map<String, Map<Integer, List<Double>>> groups = new TreeMap<>();
        for (Row row : rows) {
            if (!include.test(row)) continue;
            String key = row.algorithm + (showInput ? " / " + row.input : "");
            groups.computeIfAbsent(key, k -> new TreeMap<>())
                    .computeIfAbsent(row.n, k -> new ArrayList<>()).add(metric.applyAsDouble(row));
        }
        Map<String, List<Point>> series = new TreeMap<>();
        for (var group : groups.entrySet()) {
            List<Point> points = new ArrayList<>();
            for (var item : group.getValue().entrySet()) {
                item.getValue().sort(Comparator.naturalOrder());
                points.add(new Point(item.getKey(), item.getValue().get(item.getValue().size() / 2)));
            }
            series.put(group.getKey(), points);
        }
        int minN = series.values().stream().flatMap(List::stream).mapToInt(Point::n).min().orElseThrow();
        int maxN = series.values().stream().flatMap(List::stream).mapToInt(Point::n).max().orElseThrow();
        double minValue = series.values().stream().flatMap(List::stream).mapToDouble(Point::value).min().orElseThrow();
        double maxValue = series.values().stream().flatMap(List::stream).mapToDouble(Point::value).max().orElseThrow();
        if (minValue <= 0) throw new IllegalArgumentException("Log plots need positive measurements");
        double low = Math.floor(Math.log10(minValue));
        double high = Math.max(low + 1, Math.ceil(Math.log10(maxValue)));
        int left = 125, top = 85, width = 800, height = 420;
        BufferedImage image = new BufferedImage(1200, 610, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1200, 610);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 23));
        g.drawString(title, 65, 40);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        for (int exponent = (int) low; exponent <= high; exponent++) {
            int y = top + height - (int) ((exponent - low) / (high - low) * height);
            g.setColor(new Color(225, 225, 225));
            g.drawLine(left, y, left + width, y);
            g.setColor(Color.DARK_GRAY);
            g.drawString("1e" + exponent, left - 55, y + 5);
        }
        var ticks = new java.util.TreeSet<Integer>();
        series.values().forEach(points -> points.forEach(point -> ticks.add(point.n)));
        for (int n : ticks) {
            int x = left + (int) ((double) (n - minN) / (maxN - minN) * width);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, top, x, top + height);
            g.setColor(Color.DARK_GRAY);
            String label = Integer.toString(n);
            g.drawString(label, x - g.getFontMetrics().stringWidth(label) / 2, top + height + 28);
        }
        g.setColor(Color.BLACK);
        g.drawRect(left, top, width, height);
        g.drawString(xLabel, left + 140, 575);
        g.rotate(-Math.PI / 2);
        g.drawString(yLabel, -475, 30);
        g.rotate(Math.PI / 2);
        Color[] colors = {new Color(0, 92, 160), new Color(200, 65, 20),
                new Color(20, 135, 65), new Color(135, 55, 160)};
        int index = 0;
        for (var entry : series.entrySet()) {
            g.setColor(colors[index % colors.length]);
            // Dashed alternatives keep coincident DP series visible.
            g.setStroke(index % 2 == 0 ? new BasicStroke(3) :
                    new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10, new float[]{7, 5}, 0));
            int previousX = -1, previousY = -1;
            for (Point point : entry.getValue()) {
                int x = left + (int) ((double) (point.n - minN) / (maxN - minN) * width);
                int y = top + height - (int) ((Math.log10(point.value) - low) / (high - low) * height);
                if (previousX >= 0) g.drawLine(previousX, previousY, x, y);
                g.fillOval(x - 4, y - 4, 8, 8);
                previousX = x;
                previousY = y;
            }
            int legendY = 110 + index * 35;
            g.drawLine(950, legendY, 980, legendY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.drawString(entry.getKey(), 990, legendY + 5);
            index++;
        }
        g.dispose();
        ImageIO.write(image, "png", Path.of("results/plots", filename).toFile());
    }
}
