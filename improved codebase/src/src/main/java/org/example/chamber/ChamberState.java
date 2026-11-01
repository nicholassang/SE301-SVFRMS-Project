package org.example.chamber;

import org.example.sensor.SensorType;

import java.util.EnumMap;
import java.util.Map;

public class ChamberState {

    private final Map<SensorType, Double> readings =
            new EnumMap<>(SensorType.class);

    public ChamberState() {
        // every sensor starts at its own default, so a new sensor needs no change here
        for (SensorType sensor : SensorType.values()) {
            readings.put(sensor, sensor.getDefaultValue());
        }
    }

    public synchronized double getState(SensorType sensor) {
        return readings.get(sensor);
    }

    public synchronized void setState(SensorType sensor, double value) {
        readings.put(sensor, value);
    }

    public synchronized double adjust(
            SensorType sensor,
            double change,
            double min,
            double max) {

        double newValue = readings.get(sensor) + change;

        newValue = Math.max(min, Math.min(newValue, max));

        readings.put(sensor, newValue);

        return newValue;
    }
}
