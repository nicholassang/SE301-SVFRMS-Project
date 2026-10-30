
package org.example;

import java.util.ArrayList;
import java.util.List;

public class AlertManager {
    private static final ArrayList<String> alerts = new ArrayList<>();
    private static final ArrayList<String> archivedAlerts = new ArrayList<>();
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

    public static synchronized List<String> getArchivedAlerts() {
        return new ArrayList<>(archivedAlerts);
    }

    public static synchronized List<String> getActiveAlerts() {
        return new ArrayList<>(alerts);
    }

    public static synchronized int archiveAlerts() {
        int count = alerts.size();

        archivedAlerts.addAll(alerts);
        alerts.clear();

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
                archivedAlerts.size()
        );
    }
}
