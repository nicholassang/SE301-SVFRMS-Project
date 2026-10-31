package org.example;
import java.util.ArrayList;
import java.util.List;

public class ArchiveManager {
    private static final ArrayList<Alert> archivedAlerts = new ArrayList<>();

    public static void archiveAlerts(ArrayList<Alert> newAlerts) {
        archivedAlerts.addAll(newAlerts);
    }

    public static List<Alert> getArchivedAlerts() {
        return new ArrayList<>(archivedAlerts);
    }
}