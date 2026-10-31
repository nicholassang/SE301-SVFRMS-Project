package org.example;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.ZoneId;

public class ReportManager {
    private static final Logger logger = AppLogger.getLogger();
    private final ReportDataSource dataSource;
    private final List<Report> reportsToGenerate;

    private final AlertManager alertManager;
    private final ArchiveManager archiveManager;

    public ReportManager(
            AlertManager alertManager,
            ArchiveManager archiveManager,
            ReportDataSource dataSource,
            List<Report> reportsToGenerate) {

        this.alertManager = alertManager;
        this.archiveManager = archiveManager;
        this.dataSource = dataSource;
        this.reportsToGenerate = new ArrayList<>(reportsToGenerate);
    }

    public void generateReports() {
        for (Report report : reportsToGenerate) {
            String reportContent = report.generate();

            synchronized (SVFRMS.reportLock) {
                SVFRMS.reports.add(reportContent);
            }

            FileManager.saveReport(
                    report.getFileName(),
                    reportContent
            );

            logger.info(reportContent);
        }
    }

    public void saveArchiveReport() {
        try {
            Files.createDirectories(FileManager.ARCHIVE_DIR);

            ArrayList<String> reportLines = new ArrayList<String>();
            reportLines.add("""
                    ===== SYSTEM ARCHIVE SUMMARY =====
                    Timestamp: %s
                    Generated Alerts: %d
                    Active Alerts: %d
                    Water Usage: %d L
                    Energy Usage: %d kWh
                    Archived alerts: %d
                    ---
                    """.formatted(
                    new Date(), 
                    alertManager.getTotalAlertsGenerated(), 
                    alertManager.getActiveAlertCount(),
                    dataSource.getTotalWaterUsage(), 
                    dataSource.getTotalEnergyUsage(), 
                    archiveManager.getArchivedAlerts().size()
            ));

            List<String> chamberNames = dataSource.getChamberNames();
            if (!chamberNames.isEmpty()) {
                reportLines.add("--- chamber wise summary ---");
                for (String chamber : chamberNames) {
                    reportLines.add(SVFRMS.chambers.getChamberSummary(chamber).toReportLine(chamber));
                }
            }

            List<Alert> archivedAlerts = archiveManager.getArchivedAlerts();

            if (!archivedAlerts.isEmpty()) {
                reportLines.add("--- archived alerts ---");
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
            FileManager.saveArchiveReport(reportLines);
        } catch (Exception e) {
            logger.warning("Could not save archive report: " + e.getMessage());
        }
    }   
}