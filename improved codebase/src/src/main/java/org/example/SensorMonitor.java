package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
DOES THE 5 THINGS IN OLD processSensor()
-Logs the reading.
-Saves it in the chamber state.
-Asks the crop which rules are broken.
-For each broken rule, records an alert and adds the water or energy cost.
-Sends a device request.
*/
public class SensorMonitor {
    private ChamberRepository chambers;
    private CropRegister crops;
    private ResourceUsageTracker usage;
    private EventRecorder events;
    private final SeverityPolicy severityPolicy = new SeverityPolicy();

    public SensorMonitor(
            ChamberRepository chambers,
            CropRegister crops,
            ResourceUsageTracker usage,
            EventRecorder events) {

        this.chambers = chambers;
        this.crops = crops;
        this.usage = usage;
        this.events = events;

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

        events.logSensor("Processing " + cropName + " " + sensor + " = " + value);

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

        if (AlertManager.hasActiveAlert(chamber, cropName, ruleId)) {
            return;
        }

        String message = chamber + " " + threshold.getAlertLabel();
        double deviation = threshold.calculateDeviation(value);

        AlertSeverity severity = severityPolicy.evaluate(sensor, deviation);

        Alert alert = new Alert(
                chamber,
                cropName,
                ruleId,
                message,
                severity
        );

        events.recordAlert(alert);
    }

    private void applyControlAction(String chamber, ControlAction action) {
        usage.addWater(action.getWaterCost());
        usage.addEnergy(action.getEnergyCost());

        if (action.getDevice() != null) {
            SVFRMS.addControlRequest(chamber, action.getDevice());
        }
    }

    private void resolveRecoveredAlerts(
            String chamber,
            String cropName,
            SensorType sensor,
            ArrayList<Threshold> breaches) {

        Set<String> breachedRuleIds = new HashSet<>();

        for (Threshold t : breaches) {
            breachedRuleIds.add(t.getRuleId());
        }

        for (Alert alert : AlertManager.getActiveAlerts()) {
            boolean sameSensor =
                    alert.getType().startsWith(sensor.name() + ":");

            if (alert.matches(chamber, cropName, alert.getType())
                    && sameSensor
                    && !breachedRuleIds.contains(alert.getType())) {

                AlertManager.resolveAlert(alert);
            }
        }
    }
}
