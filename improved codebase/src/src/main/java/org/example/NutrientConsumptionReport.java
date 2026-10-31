package org.example;

import java.time.LocalDate;

public class NutrientConsumptionReport implements Report {

    private final ReportDataSource dataSource;

    public NutrientConsumptionReport(ReportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generate() {
        StringBuilder report = new StringBuilder();

        report.append("===== NUTRIENT CONSUMPTION REPORT =====\n");
        report.append("Date: ").append(LocalDate.now()).append("\n\n");

        report.append("Total Nutrient Consumption: ")
                .append(dataSource.getTotalNutrientUsage())
                .append(" units\n");

        return report.toString();
    }

    @Override
    public String getFileName() {
        return "nutrient_consumption_report.txt";
    }
}