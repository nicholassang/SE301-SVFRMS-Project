package org.example;

import java.util.ArrayList;
import java.util.List;

public class ArchiveManager {
    private final ArrayList<Alert> archivedAlerts = new ArrayList<>();

    public synchronized void archiveAlerts(List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.getStatus() == AlertStatus.RESOLVED) {
                alert.archive();
                archivedAlerts.add(alert);
            }
        }
    }

    public synchronized List<Alert> getArchivedAlerts() {
        return new ArrayList<>(archivedAlerts);
    }
}