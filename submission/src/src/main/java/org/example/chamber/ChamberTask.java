package org.example.chamber;

import org.example.sensor.SensorType;
import org.example.sensor.SensorMonitor;
import org.example.sensor.SensorDataSource;
import org.example.resource.ResourceUsageTracker;

public class ChamberTask implements Runnable {

    private final String chamber;
    private final String crop;
    private final SensorMonitor monitor;
    private final SensorDataSource sensorDataSource;
    private final ResourceUsageTracker usage;

    public ChamberTask(
            String chamber,
            String crop,
            SensorMonitor monitor,
            SensorDataSource sensorDataSource,
            ResourceUsageTracker usage) {

        this.chamber = chamber;
        this.crop = crop;
        this.monitor = monitor;
        this.sensorDataSource = sensorDataSource;
        this.usage = usage;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {

            for (SensorType sensor : SensorType.values()) {
                double value = sensorDataSource.read(
                        chamber, crop, sensor
                );

                monitor.processSensor(
                        chamber, crop, sensor, value
                );
            }

            usage.addWater(2);
            usage.addEnergy(1);
        }
    }
}
