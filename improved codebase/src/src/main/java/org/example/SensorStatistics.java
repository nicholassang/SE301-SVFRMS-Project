
package org.example;

import java.util.List;

public class SensorStatistics {
    private final double average;
    private final double minimum;
    private final double maximum;
    private final int count;

    public SensorStatistics(List<SensorReading> readings) {
        if (readings.isEmpty()) {
            throw new IllegalArgumentException("Readings cannot be empty");
        }

        double total = 0;
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (SensorReading reading : readings) {
            double value = reading.getValue();

            total += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        this.average = total / readings.size();
        this.minimum = min;
        this.maximum = max;
        this.count = readings.size();
    }

    public double getAverage() {
        return average;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public int getCount() {
        return count;
    }
}
