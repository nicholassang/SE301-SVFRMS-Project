package org.example.event;

import org.example.sensor.SensorReading;
import org.example.sensor.SensorType;

import java.util.List;

public interface EventRepository {

    void recordSensorReading(
            String chamber,
            String crop,
            SensorType sensor,
            double value
    );

    List<SensorReading> getSensorReadings();

    List<SensorReading> getSensorReadings(
            String chamber,
            SensorType sensor
    );

    void logSensor(String message);

    List<String> getSensorLogs();
}
