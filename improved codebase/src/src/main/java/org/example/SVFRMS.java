
package org.example;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SVFRMS {
    private static final Logger logger = AppLogger.getLogger();

    public static final ArrayList<String> reports = new ArrayList<>();
    public static final Object reportLock = new Object();

    static final Random random = new Random();

    public static final ChamberRepository chambers = new ChamberRepository();
    public static final ResourceUsageTracker usage = new ResourceUsageTracker();
    public static final CropRegister crops = new CropRegister();

    // Shared dependency instances
    public static final ArchiveManager archiveManager = new ArchiveManager();
    public static final AlertManager alertManager = new AlertManager(archiveManager);

    public static final EventRecorder events = new EventRecorder(chambers, alertManager);

    public static final ControlQueue controlQueue = new ControlQueue();

    public static final SensorMonitor monitor = new SensorMonitor(chambers, crops, usage, events, alertManager, controlQueue);

    public static final ControlService controlService = new ControlService(chambers, events);

    public static final ReportDataSource reportDataSource =
            new SVFRMSReportDataSource(events, chambers, usage);

    public static final ReportManager reportManager =
            new ReportManager(alertManager, archiveManager, reportDataSource);
            
    // Processes control requests from the queue
    static class ControlTask implements Runnable {
        private final ControlQueue controlQueue;
        private final ControlService controlService;

        ControlTask(ControlQueue controlQueue, ControlService controlService) {
            this.controlQueue = controlQueue;
            this.controlService = controlService;
        }

        @Override
        public void run() {
            try {
                ControlRequest request;

                while ((request = controlQueue.next()) != null) {
                    try {
                        controlService.activateControl(
                                request.chamber(),
                                request.device()
                        );
                    } catch (RuntimeException e) {
                        logger.warning(
                                "Control request failed for " + request + ": " + e
                        );
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Simulates one chamber's sensor readings
    static class ChamberTask implements Runnable {
        private final String chamber;
        private final String crop;
        private final SensorMonitor monitor;
        private final ResourceUsageTracker usage;

        ChamberTask(String chamber, String crop,
                    SensorMonitor monitor, ResourceUsageTracker usage) {
            this.chamber = chamber;
            this.crop = crop;
            this.monitor = monitor;
            this.usage = usage;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                monitor.processSensor(chamber, crop, SensorType.TEMPERATURE,
                        10 + random.nextInt(30));
                monitor.processSensor(chamber, crop, SensorType.HUMIDITY,
                        40 + random.nextInt(60));
                monitor.processSensor(chamber, crop, SensorType.SOIL,
                        20 + random.nextInt(60));
                monitor.processSensor(chamber, crop, SensorType.LIGHT,
                        300 + random.nextInt(900));
                monitor.processSensor(chamber, crop, SensorType.NUTRIENT,
                        20 + random.nextInt(80));

                usage.addWater(2);
                usage.addEnergy(1);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try (ExecutorService controlThread =
                     Executors.newSingleThreadExecutor()) {

            controlThread.execute(
                    new ControlTask(controlQueue, controlService)
            );

            try (ExecutorService chamberThreads =
                         Executors.newFixedThreadPool(4)) {

                chamberThreads.execute(
                        new ChamberTask("CH1", "Tomato", monitor, usage));
                chamberThreads.execute(
                        new ChamberTask("CH2", "Strawberry", monitor, usage));
                chamberThreads.execute(
                        new ChamberTask("CH3", "Lettuce", monitor, usage));
                chamberThreads.execute(
                        new ChamberTask("CH4", "Chili", monitor, usage));
            }

            controlQueue.stop();
        }

        alertManager.archiveAlerts();

        reportManager.generateDailyReport();
        reportManager.generateWeeklyCropReport();

        logger.info("System finished sensor processing.");
        reportManager.saveArchiveReport();
    }
}
