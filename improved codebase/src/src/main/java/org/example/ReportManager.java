package org.example;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;

public class ReportManager {
    private static final Logger logger = AppLogger.getLogger();

    public static void generateDailyReport() {
        synchronized (SVFRMS.reportLock) {
            logger.info("""
                    ===== DAILY REPORT =====
                    %s
                    Water Usage: %d L
                    Energy Usage: %d kWh
                    """.formatted(
                    AlertManager.getAlertStatistics(),
                    SVFRMS.usage.getTotalWater(),
                    SVFRMS.usage.getTotalEnergy()
            ));
            SVFRMS.reports.add("Daily Report");
        }
    }

    public static void saveArchiveReport() {
        try {
            Files.createDirectories(FileManager.ARCHIVE_DIR);

            ArrayList<String> reportLines = new ArrayList<String>();
            reportLines.add("""
                    ===== DAILY REPORT =====
                    Timestamp: %s
                    Generated Alerts: %d
                    Active Alerts: %d
                    Water Usage: %d L
                    Energy Usage: %d kWh
                    Archived alerts: %d
                    ---
                    """.formatted(
                    new Date(), 
                    AlertManager.getTotalAlertsGenerated(), 
                    AlertManager.getActiveAlertCount(),
                    SVFRMS.usage.getTotalWater(), 
                    SVFRMS.usage.getTotalEnergy(), 
                    AlertManager.getArchivedAlerts().size()
            ));
            reportLines.addAll(SVFRMS.reports);

            ArrayList<String> chamberNames = SVFRMS.chambers.getChamberNames();
            if (!chamberNames.isEmpty()) {
                reportLines.add("--- chamber wise summary ---");
                for (String chamber : chamberNames) {
                    reportLines.add(SVFRMS.chambers.getChamberSummary(chamber).toReportLine(chamber));
                }
            }

            List<String> archivedAlerts = AlertManager.getArchivedAlerts();

            if (!archivedAlerts.isEmpty()) {
                reportLines.add("--- archived alerts ---");
                reportLines.addAll(archivedAlerts);
            }

            FileManager.saveReport(reportLines);

            logger.info("Archive report saved to: " + FileManager.REPORT_FILE.toAbsolutePath());
        } catch (Exception e) {
            logger.warning("Could not save archive report: " + e.getMessage());
        }
    }   
}