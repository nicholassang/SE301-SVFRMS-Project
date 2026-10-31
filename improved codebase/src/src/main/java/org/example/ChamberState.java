package org.example;

import java.util.EnumMap;
import java.util.Map;

public class ChamberState {

    private final Map<SensorType, Double> readings =
            new EnumMap<>(SensorType.class);

    public ChamberState() {
        readings.put(SensorType.TEMPERATURE, 24.0);
        readings.put(SensorType.HUMIDITY, 70.0);
        readings.put(SensorType.SOIL, 60.0);
        readings.put(SensorType.LIGHT, 600.0);
        readings.put(SensorType.NUTRIENT, 70.0);
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