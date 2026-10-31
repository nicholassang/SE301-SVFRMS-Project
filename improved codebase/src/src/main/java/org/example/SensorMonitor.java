package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SensorMonitor {

    private final ChamberRepository chambers;
    private final CropRegister crops;
    private final ResourceUsageTracker usage;
    private final EventRecorder events;
    private final AlertManager alertManager;
    private final ControlQueue controlQueue;

    private final SeverityPolicy severityPolicy = new SeverityPolicy();

    public SensorMonitor(
            ChamberRepository chambers,
            CropRegister crops,
            ResourceUsageTracker usage,
            EventRecorder events,
            AlertManager alertManager,
            ControlQueue controlQueue) {

        this.chambers = chambers;
        this.crops = crops;
        this.usage = usage;
        this.events = events;
        this.alertManager = alertManager;
        this.controlQueue = controlQueue;

        severityPolicy.setLimits(SensorType.TEMPERATURE, 3, 7);
        severityPolicy.setLimits(SensorType.HUMIDITY, 5, 15);
        severityPolicy.setLimits(SensorType.SOIL, 5, 15);
        severityPolicy.setLimits(SensorType.LIGHT, 100, 300);
        severityPolicy.setLimits(SensorType.NUTRIENT, 5, 15);
    }

    public void processSensor(
            String chamber,
            String cropName,
            SensorType sensor,
            double value) {

        logAndUpdateState(chamber, cropName, sensor, value);

        ArrayList<Threshold> breaches =
                findBreaches(cropName, sensor, value);

        handleBreaches(chamber, cropName, sensor, value, breaches);

        resolveRecoveredAlerts(chamber, cropName, sensor, breaches);
    }

    private void logAndUpdateState(
            String chamber,
            String cropName,
            SensorType sensor,
            double value) {

        events.recordSensorReading(
                chamber,
                cropName,
                sensor,
                value
        );

        chambers.getChamberState(chamber).setState(sensor, value);
    }

    private ArrayList<Threshold> findBreaches(
            String cropName,
            SensorType sensor,
            double value) {

        ArrayList<Threshold> breaches =
                crops.getCrop(cropName).findBreaches(sensor, value);

        breaches.addAll(
                crops.getFacilityRules().findBreaches(sensor, value)
        );

        return breaches;
    }

    private void handleBreaches(
            String chamber,
            String cropName,
            SensorType sensor,
            double value,
            ArrayList<Threshold> breaches) {

        for (Threshold threshold : breaches) {
            recordBreachAlert(chamber, cropName, sensor, value, threshold);
            applyControlAction(chamber, threshold.getControlAction());
        }
    }

    private void recordBreachAlert(
            String chamber,
            String cropName,
            SensorType sensor,
            double value,
            Threshold threshold) {

        String ruleId = threshold.getRuleId();

        if (alertManager.hasActiveAlert(chamber, cropName, ruleId)) {
            return;
        }

        String message = chamber + " " + threshold.getAlertLabel();

        double deviation = threshold.calculateDeviation(value);

        AlertSeverity severity =
                severityPolicy.evaluate(sensor, deviation);

        Alert alert = new Alert(
                chamber,
                cropName,
                ruleId,
                message,
                severity
        );

        events.recordAlert(alert);
    }

    private void applyControlAction(
            String chamber,
            ControlAction action) {

        usage.addWater(action.getWaterCost());
        usage.addEnergy(action.getEnergyCost());
        usage.addNutrient(action.getNutrientCost());

        if (action.getDevice() != null) {
            controlQueue.submit(chamber, action.getDevice());
        }
    }

    private void resolveRecoveredAlerts(
            String chamber,
            String cropName,
            SensorType sensor,
            ArrayList<Threshold> breaches) {

        Set<String> breachedRuleIds = new HashSet<>();

        for (Threshold threshold : breaches) {
            breachedRuleIds.add(threshold.getRuleId());
        }

        alertManager.resolveRecoveredAlerts(
                chamber,
                cropName,
                sensor,
                breachedRuleIds
        );
    }
}