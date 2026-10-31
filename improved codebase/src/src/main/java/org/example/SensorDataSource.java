package org.example;

public interface SensorDataSource {

    double read(
            String chamber,
            String crop,
            SensorType sensor
    );
}