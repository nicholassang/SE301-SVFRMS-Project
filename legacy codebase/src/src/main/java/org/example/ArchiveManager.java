
package org.example;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ArchiveManager {
    private static final Logger logger = AppLogger.getLogger();
    
    public static void archiveAlerts() {
        ArrayList<String> toArchive = new ArrayList<>();
        synchronized (SVFRMS.alertLock) {
            if (!SVFRMS.alerts.isEmpty()) {
                toArchive.addAll(SVFRMS.alerts);
                SVFRMS.alerts.clear();
                SVFRMS.totalAlerts = 0;
            }
            synchronized (SVFRMS.reportLock) {
                if (!toArchive.isEmpty()) {
                    SVFRMS.archivedAlerts.addAll(toArchive);
                    SVFRMS.reports.add("Archived " + toArchive.size() + " alerts");
                    logger.info("Archiving alerts... " + toArchive.size() + " moved to archive.");
                } else {
                    SVFRMS.reports.add("No alerts to archive");
                    logger.warning("Archiving alerts... no active alerts to archive.");
                }
            }
        }
    }
}
