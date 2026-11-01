package org.example.app;

import org.example.logging.AppLogger;

import org.example.chamber.ChamberRepository;
import org.example.chamber.InMemoryChamberRepository;
import org.example.chamber.ChamberTask;

import org.example.control.ControlExecutor;
import org.example.control.ControlQueue;
import org.example.control.ControlService;
import org.example.control.ControlTask;

import org.example.crop.CropRegister;

import org.example.event.EventRepository;
import org.example.event.EventRecorder;
import org.example.event.InMemoryEventRepository;

import org.example.report.Report;
import org.example.report.ReportDataSource;
import org.example.report.ReportExporter;
import org.example.report.ReportManager;
import org.example.report.SVFRMSReportDataSource;
import org.example.report.TextReportExporter;
import org.example.report.DailyEnvironmentalReport;
import org.example.report.WeeklyCropReport;
import org.example.report.WaterUsageReport;
import org.example.report.EnergyConsumptionReport;
import org.example.report.NutrientConsumptionReport;

import org.example.resource.ResourceUsageTracker;

import org.example.sensor.SensorDataSource;
import org.example.sensor.SensorMonitor;
import org.example.sensor.SimulatedSensorDataSource;

import org.example.alert.ArchiveManager;
import org.example.alert.AlertManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.LinkedHashMap;
import java.util.Map;

public class SVFRMS {

    private static final Logger logger = AppLogger.getLogger();

    // Repository implementations
    public static final ChamberRepository chambers = new InMemoryChamberRepository();
    public static final EventRepository eventRepository = new InMemoryEventRepository();

    // Core resources
    public static final ResourceUsageTracker usage = new ResourceUsageTracker();
    public static final CropRegister crops = new CropRegister();

    // Sensor acquisition
    public static final SensorDataSource sensorDataSource = new SimulatedSensorDataSource();

    // Alert management
    public static final ArchiveManager archiveManager = new ArchiveManager();
    public static final AlertManager alertManager = new AlertManager(archiveManager);

    // Event recording
    public static final EventRecorder events = new EventRecorder(chambers, alertManager, eventRepository);

    // Control
    public static final ControlQueue controlQueue = new ControlQueue();
    public static final ControlExecutor controlExecutor = 
        new ControlService(
                chambers, 
                events, 
                usage
        );
    public static final SensorMonitor monitor =
        new SensorMonitor(
                chambers,
                crops,
                events,
                alertManager,
                controlQueue
        );

    // Report exporting
    public static final ReportExporter reportExporter = new TextReportExporter();

    // Report data
    public static final ReportDataSource reportDataSource =
        new SVFRMSReportDataSource(
                eventRepository,
                chambers,
                usage
        );

    // Individual reports
    public static final Report dailyEnvironmentalReport = new DailyEnvironmentalReport(reportDataSource, alertManager);
    public static final Report weeklyCropReport = new WeeklyCropReport(reportDataSource);
    public static final Report waterUsageReport = new WaterUsageReport(reportDataSource);
    public static final Report energyConsumptionReport = new EnergyConsumptionReport(reportDataSource);
    public static final Report nutrientConsumptionReport = new NutrientConsumptionReport(reportDataSource);

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

        Map<String, String> chamberCrops = new LinkedHashMap<>();
        chamberCrops.put("CH1", "Tomato");
        chamberCrops.put("CH2", "Strawberry");
        chamberCrops.put("CH3", "Lettuce");
        chamberCrops.put("CH4", "Chili");
        // chamberCrops.put("CH5", "Kale"); // uncomment to add a crop for demo

        try (ExecutorService controlThread =
                    Executors.newSingleThreadExecutor()) {

            Future<?> controlFuture = controlThread.submit(
                    new ControlTask(controlQueue, controlExecutor)
            );

            try {
                try (ExecutorService chamberThreads =
                            Executors.newFixedThreadPool(chamberCrops.size())) {

                    List<Future<?>> chamberFutures = new ArrayList<>();

                    for (Map.Entry<String, String> entry : chamberCrops.entrySet()) {
                            String chamber = entry.getKey();
                            String crop = entry.getValue();

                            if (!crops.hasCrop(crop)) {
                                    logger.warning("Skipping " + chamber + ": crop '" + crop + "' is not registered");
                                    continue;
                            }

                            chamberFutures.add(chamberThreads.submit(
                                            new ChamberTask(chamber, crop, monitor, sensorDataSource, usage)));
                    }

                    // Wait for every chamber task and propagate failures.
                    for (Future<?> future : chamberFutures) {
                        future.get();
                    }
                }

            } finally {
                // Ensure the control consumer receives its shutdown request
                // even if a chamber task fails.
                controlQueue.stop();
            }

            // Wait for all queued control requests to finish.
            controlFuture.get();
        }

        // Only generate reports after all processing has finished.
        alertManager.archiveAlerts();

        reportManager.generateReports();

        logger.info("System finished sensor processing.");

        reportManager.saveArchiveReport();
    }
}
