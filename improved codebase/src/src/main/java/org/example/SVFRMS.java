package org.example;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SVFRMS {

    private static final Logger logger = AppLogger.getLogger();

    // Repository implementations
    public static final ChamberRepository chambers =
            new InMemoryChamberRepository();

    public static final EventRepository eventRepository =
            new InMemoryEventRepository();

    // Core resources
    public static final ResourceUsageTracker usage =
            new ResourceUsageTracker();

    public static final CropRegister crops =
            new CropRegister();

    // Sensor acquisition
    public static final SensorDataSource sensorDataSource =
            new SimulatedSensorDataSource();

    // Alert management
    public static final ArchiveManager archiveManager =
            new ArchiveManager();

    public static final AlertManager alertManager =
            new AlertManager(archiveManager);

    // Event recording
    public static final EventRecorder events =
            new EventRecorder(chambers, alertManager, eventRepository);

    // Control
    public static final ControlQueue controlQueue =
            new ControlQueue();

    public static final ControlExecutor controlExecutor =
            new ControlService(chambers, events);

    // Sensor monitoring
    public static final SensorMonitor monitor =
            new SensorMonitor(
                    chambers,
                    crops,
                    usage,
                    events,
                    alertManager,
                    controlQueue
            );

    // Report exporting
    public static final ReportExporter reportExporter =
            new TextReportExporter();

    // Report data
    public static final ReportDataSource reportDataSource =
            new SVFRMSReportDataSource(
                    eventRepository,
                    chambers,
                    usage
            );

    // Individual reports
    public static final Report dailyEnvironmentalReport =
            new DailyEnvironmentalReport(reportDataSource, alertManager);

    public static final Report weeklyCropReport =
            new WeeklyCropReport(reportDataSource);

    public static final Report waterUsageReport =
            new WaterUsageReport(reportDataSource);

    public static final Report energyConsumptionReport =
            new EnergyConsumptionReport(reportDataSource);

    public static final Report nutrientConsumptionReport =
            new NutrientConsumptionReport(reportDataSource);

    // Report management
    public static final ReportManager reportManager =
            new ReportManager(
                    alertManager,
                    archiveManager,
                    reportDataSource,
                    reportExporter,
                    List.of(
                            dailyEnvironmentalReport,
                            weeklyCropReport,
                            waterUsageReport,
                            energyConsumptionReport,
                            nutrientConsumptionReport
                    )
            );

    public static void main(String[] args) throws Exception {

        try (ExecutorService controlThread =
                     Executors.newSingleThreadExecutor()) {

            controlThread.execute(
                    new ControlTask(controlQueue, controlExecutor)
            );

            try (ExecutorService chamberThreads =
                         Executors.newFixedThreadPool(4)) {

                chamberThreads.execute(
                        new ChamberTask(
                                "CH1", "Tomato",
                                monitor, sensorDataSource, usage
                        )
                );

                chamberThreads.execute(
                        new ChamberTask(
                                "CH2", "Strawberry",
                                monitor, sensorDataSource, usage
                        )
                );

                chamberThreads.execute(
                        new ChamberTask(
                                "CH3", "Lettuce",
                                monitor, sensorDataSource, usage
                        )
                );

                chamberThreads.execute(
                        new ChamberTask(
                                "CH4", "Chili",
                                monitor, sensorDataSource, usage
                        )
                );
            }

            // All chamber tasks have finished; process remaining requests.
            controlQueue.stop();
        }

        alertManager.archiveAlerts();

        reportManager.generateReports();

        logger.info("System finished sensor processing.");

        reportManager.saveArchiveReport();
    }
}