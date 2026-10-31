package org.example.event;

import org.example.sensor.SensorReading;
import org.example.sensor.SensorType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InMemoryEventRepository implements EventRepository {

    private final ArrayList<SensorReading> sensorReadings =
            new ArrayList<>();

    private final ArrayList<String> sensorLogs =
            new ArrayList<>();

    @Override
    public synchronized void recordSensorReading(
            String chamber,
            String crop,
            SensorType sensor,
            double value) {

        sensorReadings.add(
                new SensorReading(chamber, crop, sensor, value)
        );

        sensorLogs.add(
                new Date() + " : Processing " + crop
                + " " + sensor + " = " + value
        );
    }

    @Override
    public synchronized List<SensorReading> getSensorReadings() {
        return new ArrayList<>(sensorReadings);
    }

    @Override
    public synchronized List<SensorReading> getSensorReadings(
            String chamber,
            SensorType sensor) {

        ArrayList<SensorReading> result = new ArrayList<>();

        for (SensorReading reading : sensorReadings) {
            if (reading.getChamber().equals(chamber)
                    && reading.getSensor() == sensor) {
                result.add(reading);
            }
        }

        return result;
    }

    @Override
    public synchronized void logSensor(String message) {
        sensorLogs.add(new Date() + " : " + message);
    }

    @Override
    public synchronized List<String> getSensorLogs() {
        return new ArrayList<>(sensorLogs);
    }
}
