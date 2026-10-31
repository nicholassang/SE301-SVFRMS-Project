package org.example.report;

import org.example.alert.AlertManager;
import org.example.sensor.SensorType;
import org.example.sensor.SensorReading;
import org.example.sensor.SensorStatistics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class DailyEnvironmentalReport implements Report {

    private final ReportDataSource dataSource;
    private final AlertManager alertManager;

    public DailyEnvironmentalReport(
            ReportDataSource dataSource,
            AlertManager alertManager) {
        this.dataSource = dataSource;
        this.alertManager = alertManager;
    }

    @Override
    public String generate() {
        LocalDate today = LocalDate.now();
        StringBuilder report = new StringBuilder();

        report.append("\n===== DAILY ENVIRONMENTAL REPORT =====\n");
        report.append("Date: ").append(today).append("\n\n");
        report.append(alertManager.getAlertStatistics()).append("\n\n");

        for (String chamber : dataSource.getChamberNames()) {
            report.append("--- Chamber: ")
                    .append(chamber)
                    .append(" ---\n");

            for (SensorType sensor : SensorType.values()) {
                ArrayList<SensorReading> dailyReadings = new ArrayList<>();

                for (SensorReading reading : dataSource.getSensorReadings()) {
                    LocalDate readingDate = reading.getTimestamp()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    if (reading.getChamber().equals(chamber)
                            && reading.getSensor() == sensor
                            && readingDate.equals(today)) {
                        dailyReadings.add(reading);
                    }
                }

                if (dailyReadings.isEmpty()) {
                    continue;
                }

                SensorStatistics stats =
                        new SensorStatistics(dailyReadings);

                report.append(String.format(
                        "%s: Average=%.2f, Min=%.2f, Max=%.2f, Readings=%d%n",
                        sensor,
                        stats.getAverage(),
                        stats.getMinimum(),
                        stats.getMaximum(),
                        stats.getCount()
                ));
            }

            report.append("\n");
        }

        report.append("Water Usage: ")
                .append(dataSource.getTotalWaterUsage())
                .append(" L\n");

        report.append("Energy Usage: ")
                .append(dataSource.getTotalEnergyUsage())
                .append(" kWh\n");

        return report.toString();
    }

    @Override
    public String getFileName() {
        return "daily_environmental_report.txt";
    }
}
