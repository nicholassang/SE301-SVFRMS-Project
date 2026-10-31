package org.example.sensor;

public interface SensorDataSource {

    double read(
            String chamber,
            String crop,
            SensorType sensor
    );
}
