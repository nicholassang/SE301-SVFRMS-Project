package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Set;

public class AlertManager {
    private static final Logger logger = AppLogger.getLogger();

    private static final ArrayList<Alert> alerts = new ArrayList<>();
    private static int totalAlertsGenerated = 0;

    public static synchronized boolean hasActiveAlert(
            String chamber,
            String crop,
            String ruleId) {

        return alerts.stream().anyMatch(alert ->
                alert.getStatus() == AlertStatus.ACTIVE
                && alert.matches(chamber, crop, ruleId)
        );
    }

    public static synchronized void resolveRecoveredAlerts(
            String chamber,
            String crop,
            SensorType sensor,
            Set<String> breachedRuleIds) {

        for (Alert alert : alerts) {
            boolean sameChamberAndCrop =
                    alert.matches(chamber, crop, alert.getType());

            boolean sameSensor =
                    alert.getType().startsWith(sensor.name() + ":");

            boolean recovered =
                    !breachedRuleIds.contains(alert.getType());

            if (alert.getStatus() == AlertStatus.ACTIVE
                    && sameChamberAndCrop
                    && sameSensor
                    && recovered) {

                alert.resolve();
            }
        }
    }

    public static synchronized void addAlert(Alert alert) {
        alerts.add(alert);
        totalAlertsGenerated++;
    }

    public static synchronized void resolveAlert(Alert alert) {
        if (alerts.contains(alert)) {
            alert.resolve();
        }
    }

    public static synchronized int getActiveAlertCount() {
        return (int) alerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.ACTIVE)
                .count();
    }

    public static synchronized int getTotalAlertsGenerated() {
        return totalAlertsGenerated;
    }

    public static synchronized List<Alert> getActiveAlerts() {
        return alerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.ACTIVE)
                .toList();
    }

    public static synchronized int archiveAlerts() {
        ArrayList<Alert> resolvedAlerts = new ArrayList<>();

        for (Alert alert : alerts) {
            if (alert.getStatus() == AlertStatus.RESOLVED) {
                resolvedAlerts.add(alert);
            }
        }

        ArchiveManager.archiveAlerts(resolvedAlerts);

        alerts.removeIf(
                a -> a.getStatus() == AlertStatus.ARCHIVED
        );

        logger.info(resolvedAlerts.size() + " resolved alerts archived.");

        return resolvedAlerts.size();
    }

    public static synchronized String getAlertStatistics() {
        return """
                Generated Alerts: %d
                Active Alerts: %d
                Resolved Alerts: %d
                Archived Alerts: %d
                """.formatted(
                totalAlertsGenerated,
                getActiveAlertCount(),
                alerts.size() - getActiveAlertCount(),
                ArchiveManager.getArchivedAlerts().size()
        );
    }
}