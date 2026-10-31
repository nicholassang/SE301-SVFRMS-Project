package org.example;

import java.util.ArrayList;
import java.util.List;

public class ArchiveManager {
    private static final ArrayList<Alert> archivedAlerts = new ArrayList<>();

    public static synchronized void archiveAlerts(List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.getStatus() == AlertStatus.RESOLVED) {
                alert.archive();
                archivedAlerts.add(alert);
            }
        }
    }

    public static synchronized List<Alert> getArchivedAlerts() {
        return new ArrayList<>(archivedAlerts);
    }
}