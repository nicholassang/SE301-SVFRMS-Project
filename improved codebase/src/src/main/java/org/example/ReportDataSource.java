package org.example;

import java.util.List;

public interface ReportDataSource {
    List<SensorReading> getSensorReadings();
    List<String> getChamberNames();
    int getTotalWaterUsage();
    int getTotalEnergyUsage();
}