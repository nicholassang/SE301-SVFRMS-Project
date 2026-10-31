package org.example;

import java.util.List;

public interface ReportDataSource {
    List<SensorReading> getSensorReadings();
    List<String> getChamberNames();
    ChamberSummary getChamberSummary(String chamber);
    int getTotalWaterUsage();
    int getTotalEnergyUsage();
    int getTotalNutrientUsage();
}