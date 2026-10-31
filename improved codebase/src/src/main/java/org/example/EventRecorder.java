package org.example;

import java.util.logging.Logger;

public class EventRecorder {

    private static final Logger logger = AppLogger.getLogger();

    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private final AlertManager alertManager;
    private final ChamberRepository chambers;
    private final EventRepository eventRepository;

    public EventRecorder(
            ChamberRepository chambers,
            AlertManager alertManager,
            EventRepository eventRepository) {

        this.chambers = chambers;
        this.alertManager = alertManager;
        this.eventRepository = eventRepository;
    }

    public void recordSensorReading(
            String chamber,
            String crop,
            SensorType sensor,
            double value) {

        eventRepository.recordSensorReading(
                chamber, crop, sensor, value
        );
    }

    public void logSensor(String message) {
        eventRepository.logSensor(message);
    }

    public void recordAlert(Alert alert) {
        String message = alert.getMessage();

        chambers.getChamberSummary(alert.getChamber())
                .recordAlert(message);

        alertManager.addAlert(alert);

        logger.info(
                "[" + alert.getChamber() + "]"
                + "[" + alert.getCrop() + "]"
                + "[" + alert.getSeverity() + "] "
                + message
        );
    }

    public void recordAction(
            String chamber,
            String action,
            String result) {

        chambers.getChamberSummary(chamber).recordAction(action);

        logger.info(
                "\t" + GREEN + "[" + chamber + "][CONTROL] "
                + action + RESET
        );

        logger.info(
                "\t" + GREEN + "[" + chamber + "][CONTROL] "
                + result + RESET
        );
    }

    public java.util.List<SensorReading> getSensorReadings() {
        return eventRepository.getSensorReadings();
    }

    public java.util.List<SensorReading> getSensorReadings(
            String chamber,
            SensorType sensor) {

        return eventRepository.getSensorReadings(chamber, sensor);
    }

    public java.util.List<String> getSensorLogs() {
        return eventRepository.getSensorLogs();
    }
}