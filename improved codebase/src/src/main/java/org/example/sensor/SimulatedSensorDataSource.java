package org.example.sensor;

import java.util.Random;

public class SimulatedSensorDataSource implements SensorDataSource {

    private final Random random = new Random();

    @Override
    public double read(
            String chamber,
            String crop,
            SensorType sensor) {

        return switch (sensor) {
            case TEMPERATURE -> 10 + random.nextInt(30);
            case HUMIDITY -> 40 + random.nextInt(60);
            case SOIL -> 20 + random.nextInt(60);
            case LIGHT -> 300 + random.nextInt(900);
            case NUTRIENT -> 20 + random.nextInt(80);
        };
    }
}
