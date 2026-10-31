package org.example;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.ZoneId;

public class ReportManager {
    private static final Logger logger = AppLogger.getLogger();
    private final ReportDataSource dataSource;
    private final List<Report> reportsToGenerate;

    private final AlertManager alertManager;
    private final ArchiveManager archiveManager;

    public ReportManager(
            AlertManager alertManager,
            ArchiveManager archiveManager,
            ReportDataSource dataSource,
            List<Report> reportsToGenerate) {

        this.alertManager = alertManager;
        this.archiveManager = archiveManager;
        this.dataSource = dataSource;
        this.reportsToGenerate = new ArrayList<>(reportsToGenerate);
    }

    public void generateReports() {
        for (Report report : reportsToGenerate) {
            String reportContent = report.generate();

            synchronized (SVFRMS.reportLock) {
                SVFRMS.reports.add(reportContent);
            }

            logger.info(reportContent);
        }
    }

    public void generateDailyReport() {
        LocalDate today = LocalDate.now();
        StringBuilder report = new StringBuilder();

        report.append("===== DAILY ENVIRONMENTAL REPORT =====\n");
        report.append("Date: ").append(today).append("\n\n");

        report.append(alertManager.getAlertStatistics()).append("\n\n");

        for (String chamber : dataSource.getChamberNames()) {
            report.append("--- Chamber: ")
                    .append(chamber)
                    .append(" ---\n");

            for (SensorType sensor : SensorType.values()) {
                ArrayList<SensorReading> dailyReadings = new ArrayList<>();

                for (SensorReading reading :
                        SVFRMS.events.getSensorReadings(chamber, sensor)) {

                    LocalDate readingDate = reading.getTimestamp()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    if (readingDate.equals(today)) {
                        dailyReadings.add(reading);
                    }
                }

                if (dailyReadings.isEmpty()) {
                    continue;
                }

                SensorStatistics stats =
                        new SensorStatistics(dailyReadings);

                report.append(String.format(
                        "%s: Average=%.2f, Min=%.2f, Max=%.2f, Readings=%d%n",
                        sensor,
                        stats.getAverage(),
                        stats.getMinimum(),
                        stats.getMaximum(),
                        stats.getCount()
                ));
            }

            report.append("\n");
        }

        report.append("Water Usage: ")
                .append(dataSource.getTotalWaterUsage())
                .append(" L\n");

        report.append("Energy Usage: ")
                .append(dataSource.getTotalEnergyUsage())
                .append(" kWh\n");

        synchronized (SVFRMS.reportLock) {
            SVFRMS.reports.add(report.toString());
        }

        logger.info(report.toString());
    }

    public void generateWeeklyCropReport() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        Map<String, EnumMap<SensorType, ArrayList<SensorReading>>> cropReadings =
                new TreeMap<>();

        for (SensorReading reading : dataSource.getSensorReadings()) {
            LocalDate readingDate = reading.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (readingDate.isBefore(startDate) || readingDate.isAfter(today)) {
                continue;
            }

            cropReadings
                    .computeIfAbsent(reading.getCrop(),
                            key -> new EnumMap<>(SensorType.class))
                    .computeIfAbsent(reading.getSensor(),
                            key -> new ArrayList<>())
                    .add(reading);
        }

        StringBuilder report = new StringBuilder();

        report.append("===== WEEKLY CROP REPORT =====\n");
        report.append("Period: ").append(startDate)
                .append(" to ").append(today).append("\n\n");

        if (cropReadings.isEmpty()) {
            report.append("No sensor readings available for this period.\n");
        }

        for (Map.Entry<String, EnumMap<SensorType, ArrayList<SensorReading>>> cropEntry
                : cropReadings.entrySet()) {

            report.append("--- Crop: ")
                    .append(cropEntry.getKey())
                    .append(" ---\n");

            for (Map.Entry<SensorType, ArrayList<SensorReading>> sensorEntry
                    : cropEntry.getValue().entrySet()) {

                SensorStatistics stats = new SensorStatistics(sensorEntry.getValue());

                report.append(String.format(
                        "%s: Average=%.2f, Min=%.2f, Max=%.2f, Readings=%d%n",
                        sensorEntry.getKey(),
                        stats.getAverage(),
                        stats.getMinimum(),
                        stats.getMaximum(),
                        stats.getCount()
                ));
            }

            report.append("\n");
        }

        synchronized (SVFRMS.reportLock) {
            SVFRMS.reports.add(report.toString());
        }

        logger.info(report.toString());
    }

    public void saveArchiveReport() {
        try {
            Files.createDirectories(FileManager.ARCHIVE_DIR);

            ArrayList<String> reportLines = new ArrayList<String>();
            reportLines.add("""
                    ===== DAILY REPORT =====
                    Timestamp: %s
                    Generated Alerts: %d
                    Active Alerts: %d
                    Water Usage: %d L
                    Energy Usage: %d kWh
                    Archived alerts: %d
                    ---
                    """.formatted(
                    new Date(), 
                    alertManager.getTotalAlertsGenerated(), 
                    alertManager.getActiveAlertCount(),
                    dataSource.getTotalWaterUsage(), 
                    dataSource.getTotalEnergyUsage(), 
                    archiveManager.getArchivedAlerts().size()
            ));
            // same lock as generateDailyReport(), every read and write of reports must hold it
            synchronized (SVFRMS.reportLock) {
                reportLines.addAll(SVFRMS.reports);
            }

            List<String> chamberNames = dataSource.getChamberNames();
            if (!chamberNames.isEmpty()) {
                reportLines.add("--- chamber wise summary ---");
                for (String chamber : chamberNames) {
                    reportLines.add(SVFRMS.chambers.getChamberSummary(chamber).toReportLine(chamber));
                }
            }

            List<Alert> archivedAlerts = archiveManager.getArchivedAlerts();

            if (!archivedAlerts.isEmpty()) {
                reportLines.add("--- archived alerts ---");
                for (Alert alert : archivedAlerts) {
                    reportLines.add(
                            "[" + alert.getSeverity() + "] "
                            + "[" + alert.getStatus() + "] "
                            + "[" + alert.getChamber() + "] "
                            + "[" + alert.getCrop() + "] "
                            + alert.getMessage()
                    );
                }
            }

            FileManager.saveReport(reportLines);

            logger.info("Archive report saved to: " + FileManager.REPORT_FILE.toAbsolutePath());
        } catch (Exception e) {
            logger.warning("Could not save archive report: " + e.getMessage());
        }
    }   
}