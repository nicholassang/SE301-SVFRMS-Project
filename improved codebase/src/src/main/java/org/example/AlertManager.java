
package org.example;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

public class AlertManager {
    private static final Logger logger = AppLogger.getLogger();

    private static final ArrayList<String> alerts = new ArrayList<>();
    private static int totalAlertsGenerated = 0;

    public static synchronized void addAlert(String message) {
        alerts.add(message);
        totalAlertsGenerated++;
    }

    public static synchronized int getActiveAlertCount() {
        return alerts.size();
    }

    public static synchronized int getTotalAlertsGenerated() {
        return totalAlertsGenerated;
    }

    public static synchronized List<String> getActiveAlerts() {
        return new ArrayList<>(alerts);
    }

    public static synchronized int archiveAlerts() {
        int count = alerts.size();

        if (count > 0) {
            ArchiveManager.archiveAlerts(alerts);
            alerts.clear();

            logger.info(count + " alerts successfully archived.");
        } else {
            logger.info("No active alerts to archive.");
        }

        return count;
    }

    public static synchronized String getAlertStatistics() {
        return """
                Generated Alerts: %d
                Active Alerts: %d
                Archived Alerts: %d
                """.formatted(
                totalAlertsGenerated,
                alerts.size(),
                ArchiveManager.getArchivedAlerts().size()
        );
    }
}
