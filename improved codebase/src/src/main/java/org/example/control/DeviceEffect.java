package org.example.control;

import org.example.sensor.SensorType;

// one reading a device moves: e.g. TEMPERATURE by -5, never below 18
// a record is immutable, so every thread can share it without locking
public record DeviceEffect(
        SensorType sensor,      // e.g. TEMPERATURE
        double change,          // e.g. -5
        double min,
        double max,
        String messageStart,    // e.g. "Temperature reduced to "
        String unit) {          // e.g. "C"

    public DeviceEffect {
        if (sensor == null) {
            throw new IllegalArgumentException("sensor must not be null");
        }
    }
}
