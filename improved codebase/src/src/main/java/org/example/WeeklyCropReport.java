package org.example;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

public class WeeklyCropReport implements Report {

    private final ReportDataSource dataSource;

    public WeeklyCropReport(ReportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generate() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        Map<String, EnumMap<SensorType, ArrayList<SensorReading>>> cropReadings =
                new TreeMap<>();

        for (SensorReading reading : dataSource.getSensorReadings()) {
            LocalDate readingDate = reading.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (readingDate.isBefore(startDate) || readingDate.isAfter(today)) {
                continue;
            }

            cropReadings
                    .computeIfAbsent(reading.getCrop(),
                            key -> new EnumMap<>(SensorType.class))
                    .computeIfAbsent(reading.getSensor(),
                            key -> new ArrayList<>())
                    .add(reading);
        }

        StringBuilder report = new StringBuilder();

        report.append("===== WEEKLY CROP REPORT =====\n");
        report.append("Period: ").append(startDate)
                .append(" to ").append(today).append("\n\n");

        if (cropReadings.isEmpty()) {
            report.append("No sensor readings available for this period.\n");
        }

        for (Map.Entry<String, EnumMap<SensorType, ArrayList<SensorReading>>> cropEntry
                : cropReadings.entrySet()) {

            report.append("--- Crop: ")
                    .append(cropEntry.getKey())
                    .append(" ---\n");

            for (Map.Entry<SensorType, ArrayList<SensorReading>> sensorEntry
                    : cropEntry.getValue().entrySet()) {

                SensorStatistics stats =
                        new SensorStatistics(sensorEntry.getValue());

                report.append(String.format(
                        "%s: Average=%.2f, Min=%.2f, Max=%.2f, Readings=%d%n",
                        sensorEntry.getKey(),
                        stats.getAverage(),
                        stats.getMinimum(),
                        stats.getMaximum(),
                        stats.getCount()
                ));
            }

            report.append("\n");
        }

        return report.toString();
    }

    @Override
    public String getFileName() {
        return "weekly_crop_report.txt";
    }
}