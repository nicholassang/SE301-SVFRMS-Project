
package org.example.sensor;

import java.time.Instant;

public class SensorReading {
    private final Instant timestamp;
    private final String chamber;
    private final String crop;
    private final SensorType sensor;
    private final double value;

    public SensorReading(
            String chamber,
            String crop,
            SensorType sensor,
            double value) {

        this.timestamp = Instant.now();
        this.chamber = chamber;
        this.crop = crop;
        this.sensor = sensor;
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getChamber() {
        return chamber;
    }

    public String getCrop() {
        return crop;
    }

    public SensorType getSensor() {
        return sensor;
    }

    public double getValue() {
        return value;
    }
}
