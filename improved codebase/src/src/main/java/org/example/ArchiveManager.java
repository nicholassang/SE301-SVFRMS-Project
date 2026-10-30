package org.example;

import java.util.logging.Logger;

public class ArchiveManager {
    private static final Logger logger = AppLogger.getLogger();

    public static void archiveAlerts() {
        int count = AlertManager.archiveAlerts();
        if (count > 0) {
            logger.info(count + " alerts successfully archived.");
        } else {
            logger.info("No active alerts to archive.");
        }
    }
}