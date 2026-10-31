package org.example;

import java.time.LocalDate;

public class EnergyConsumptionReport implements Report {

    private final ReportDataSource dataSource;

    public EnergyConsumptionReport(ReportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generate() {
        StringBuilder report = new StringBuilder();

        report.append("===== ENERGY CONSUMPTION REPORT =====\n");
        report.append("Date: ").append(LocalDate.now()).append("\n\n");

        report.append("Total Energy Consumption: ")
                .append(dataSource.getTotalEnergyUsage())
                .append(" kWh\n");

        return report.toString();
    }
}