package org.example;

import java.util.List;

public class SVFRMSReportDataSource implements ReportDataSource {

    private final EventRepository events;
    private final ChamberRepository chambers;
    private final ResourceUsageTracker usage;

    public SVFRMSReportDataSource(
            EventRepository events,
            ChamberRepository chambers,
            ResourceUsageTracker usage) {

        this.events = events;
        this.chambers = chambers;
        this.usage = usage;
    }

    @Override
    public List<SensorReading> getSensorReadings() {
        return events.getSensorReadings();
    }

    @Override
    public List<String> getChamberNames() {
        return chambers.getChamberNames();
    }

    @Override
    public ChamberSummary getChamberSummary(String chamber) {
        return chambers.getChamberSummary(chamber);
    }

    @Override
    public int getTotalWaterUsage() {
        return usage.getTotalWater();
    }

    @Override
    public int getTotalEnergyUsage() {
        return usage.getTotalEnergy();
    }

    @Override
    public int getTotalNutrientUsage() {
        return usage.getTotalNutrient();
    }
}