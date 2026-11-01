package org.example.sensor;

import java.util.Random;

public class SimulatedSensorDataSource implements SensorDataSource {

    private final Random random = new Random();

    @Override
    public double read(
            String chamber,
            String crop,
            SensorType sensor) {

        // each sensor knows its own range, so a new sensor needs no change here
        // (Random is thread-safe, so the 4 chamber threads can share one instance)
        ReadingRange range = sensor.getSimulatedRange();
        return range.min() + random.nextInt(range.max() - range.min());
    }
}
