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
                    SVFRMS.totalWaterUsage,
                    SVFRMS.totalEnergyUsage
            ));
            SVFRMS.reports.add("Daily Report");
        }
    }

    public static void saveArchiveReport() {
        try {
            Files.createDirectories(SVFRMS.ARCHIVE_DIR);

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
                    SVFRMS.totalWaterUsage, 
                    SVFRMS.totalEnergyUsage, 
                    AlertManager.getArchivedAlerts().size()
            ));
            reportLines.addAll(SVFRMS.reports);

            if (SVFRMS.chamberSummaries.size() > 0) {
                reportLines.add("--- chamber wise summary ---");
                TreeSet<String> sortedChambers = new TreeSet<String>(SVFRMS.chamberSummaries.keySet());
                for (String chamber : sortedChambers) {
                    SVFRMS.ChamberSummary summary = SVFRMS.chamberSummaries.get(chamber);
                    reportLines.add(chamber + " -> Alerts=" + summary.alerts + ", Actions=" + summary.mitigationActions + ", Last Action=" + summary.lastAction + ", Last Alert=" + summary.lastAlert);
                }
            }

            List<String> archivedAlerts = AlertManager.getArchivedAlerts();

            if (!archivedAlerts.isEmpty()) {
                reportLines.add("--- archived alerts ---");
                reportLines.addAll(archivedAlerts);
            }

            FileManager.saveReport(reportLines);

            logger.info("Archive report saved to: " + SVFRMS.REPORT_FILE.toAbsolutePath());
        } catch (Exception e) {
            logger.warning("Could not save archive report: " + e.getMessage());
        }
    }   
}