package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Set;

public class AlertManager {
    private final ArchiveManager archiveManager;

    private static final Logger logger = AppLogger.getLogger();

    private final ArrayList<Alert> alerts = new ArrayList<>();
    private int totalAlertsGenerated = 0;

    public AlertManager(ArchiveManager archiveManager) {
        this.archiveManager = archiveManager;
    }

    public synchronized boolean hasActiveAlert(
            String chamber,
            String crop,
            String ruleId) {

        return alerts.stream().anyMatch(alert ->
                alert.getStatus() == AlertStatus.ACTIVE
                && alert.matches(chamber, crop, ruleId)
        );
    }

    public synchronized void resolveRecoveredAlerts(
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

    public synchronized void addAlert(Alert alert) {
        alerts.add(alert);
        totalAlertsGenerated++;
    }

    public synchronized void resolveAlert(Alert alert) {
        if (alerts.contains(alert)) {
            alert.resolve();
        }
    }

    public synchronized int getActiveAlertCount() {
        return (int) alerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.ACTIVE)
                .count();
    }

    public synchronized int getTotalAlertsGenerated() {
        return totalAlertsGenerated;
    }

    public synchronized List<Alert> getActiveAlerts() {
        return alerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.ACTIVE)
                .toList();
    }

    public synchronized int archiveAlerts() {
        ArrayList<Alert> resolvedAlerts = new ArrayList<>();

        for (Alert alert : alerts) {
            if (alert.getStatus() == AlertStatus.RESOLVED) {
                resolvedAlerts.add(alert);
            }
        }

        archiveManager.archiveAlerts(resolvedAlerts);

        alerts.removeIf(
                a -> a.getStatus() == AlertStatus.ARCHIVED
        );

        logger.info(resolvedAlerts.size() + " resolved alerts archived.");

        return resolvedAlerts.size();
    }

    public synchronized String getAlertStatistics() {
        return """
                Generated Alerts: %d
                Active Alerts: %d
                Resolved Alerts: %d
                Archived Alerts: %d
                """.formatted(
                totalAlertsGenerated,
                getActiveAlertCount(),
                alerts.size() - getActiveAlertCount(),
                archiveManager.getArchivedAlerts().size()
        );
    }
}