
package org.example;

import java.util.logging.Logger;

public class ReportManager {
    private static final Logger logger = AppLogger.getLogger();

    public static void generateDailyReport() {
        synchronized (SVFRMS.alertLock) {
            synchronized (SVFRMS.reportLock) {
                logger.info("""
                        ===== DAILY REPORT =====
                        Generated Alerts: %d
                        Active Alerts: %d
                        Water Usage: %d L
                        Energy Usage: %d kWh
                        """.formatted(
                        SVFRMS.totalAlertsGenerated,
                        SVFRMS.totalAlerts,
                        SVFRMS.totalWaterUsage,
                        SVFRMS.totalEnergyUsage
                ));
                SVFRMS.reports.add("Daily Report");
            }
        }
    }
}
