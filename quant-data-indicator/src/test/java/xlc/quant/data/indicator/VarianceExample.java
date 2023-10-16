package xlc.quant.data.indicator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class VarianceExample {
    public static void main(String[] args) {
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (double value : data) {
            stats.addValue(value);
        }

        double variance = stats.getVariance();
        System.out.println("Variance: " + variance);
    }
}
