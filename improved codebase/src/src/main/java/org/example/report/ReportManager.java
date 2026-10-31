package org.example.report;

import org.example.alert.Alert;
import org.example.alert.AlertManager;
import org.example.alert.ArchiveManager;
import org.example.logging.AppLogger;

import java.util.*;
import java.util.logging.Logger;

public class ReportManager {

    private static final Logger logger = AppLogger.getLogger();

    private final ReportDataSource dataSource;
    private final ReportExporter reportExporter;
    private final List<Report> reportsToGenerate;

    private final AlertManager alertManager;
    private final ArchiveManager archiveManager;

    public ReportManager(
            AlertManager alertManager,
            ArchiveManager archiveManager,
            ReportDataSource dataSource,
            ReportExporter reportExporter,
            List<Report> reportsToGenerate) {

        this.alertManager = alertManager;
        this.archiveManager = archiveManager;
        this.dataSource = dataSource;
        this.reportExporter = reportExporter;
        this.reportsToGenerate = new ArrayList<>(reportsToGenerate);
    }

    public void generateReports() {

        for (Report report : reportsToGenerate) {

            String reportContent = report.generate();

            reportExporter.export(
                    report.getFileName(),
                    reportContent
            );

            logger.info(reportContent);
        }
    }

    public void saveArchiveReport() {

        try {
            ArrayList<String> reportLines = new ArrayList<>();

            reportLines.add("""
                    ===== SYSTEM ARCHIVE SUMMARY =====
                    Timestamp: %s
                    Generated Alerts: %d
                    Active Alerts: %d
                    Water Usage: %d L
                    Energy Usage: %d kWh
                    Nutrient Usage: %d
                    Archived Alerts: %d
                    ---
                    """.formatted(
                    new Date(),
                    alertManager.getTotalAlertsGenerated(),
                    alertManager.getActiveAlertCount(),
                    dataSource.getTotalWaterUsage(),
                    dataSource.getTotalEnergyUsage(),
                    dataSource.getTotalNutrientUsage(),
                    archiveManager.getArchivedAlerts().size()
            ));

            List<String> chamberNames = dataSource.getChamberNames();

            if (!chamberNames.isEmpty()) {

                reportLines.add("--- CHAMBER SUMMARIES ---");

                for (String chamber : chamberNames) {
                    reportLines.add(
                        dataSource.getChamberSummary(chamber)
                                .toReportLine(chamber)
                    );
                }
            }

            List<Alert> archivedAlerts =
                    archiveManager.getArchivedAlerts();

            if (!archivedAlerts.isEmpty()) {

                reportLines.add("--- ARCHIVED ALERTS ---");

                for (Alert alert : archivedAlerts) {
                    reportLines.add(
                            "[" + alert.getSeverity() + "] "
                            + "[" + alert.getStatus() + "] "
                            + "[" + alert.getChamber() + "] "
                            + "[" + alert.getCrop() + "] "
                            + alert.getMessage()
                    );
                }
            }

            reportExporter.export(
                    "archive_report.txt",
                    String.join(System.lineSeparator(), reportLines)
            );

        } catch (Exception e) {
            logger.warning(
                    "Could not save archive report: " + e.getMessage()
            );
        }
    }
}
