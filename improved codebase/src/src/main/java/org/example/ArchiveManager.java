package org.example;
import java.util.ArrayList;
import java.util.List;

public class ArchiveManager {
    private static final ArrayList<String> archivedAlerts = new ArrayList<>();

    public static void archiveAlerts(ArrayList<String> newAlerts) {
        archivedAlerts.addAll(newAlerts);
    }

    public static List<String> getArchivedAlerts() {
        return new ArrayList<>(archivedAlerts);
    }
}