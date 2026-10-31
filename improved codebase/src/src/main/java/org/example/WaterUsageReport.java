package org.example;

import java.time.LocalDate;

public class WaterUsageReport implements Report {

    private final ReportDataSource dataSource;

    public WaterUsageReport(ReportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generate() {
        StringBuilder report = new StringBuilder();

        report.append("===== WATER USAGE REPORT =====\n");
        report.append("Date: ").append(LocalDate.now()).append("\n\n");

        report.append("Total Water Consumption: ")
                .append(dataSource.getTotalWaterUsage())
                .append(" L\n");

        return report.toString();
    }

    @Override
    public String getFileName() {
        return "water_usage_report.txt";
    }
}