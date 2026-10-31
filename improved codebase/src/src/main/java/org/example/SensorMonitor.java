package org.example;

import java.util.ArrayList;

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

    public SensorMonitor(ChamberRepository chambers, CropRegister crops, ResourceUsageTracker usage, EventRecorder events){
        this.chambers = chambers;
        this.crops = crops;
        this.usage = usage;
        this.events = events;
    }

    public void processSensor(
            String chamber,
            String cropName,
            SensorType sensor,
            double value) {

        logAndUpdateState(chamber, cropName, sensor, value);

        ArrayList<Threshold> breaches =
                findBreaches(cropName, sensor, value);

        handleBreaches(chamber, cropName, breaches);
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
                crops.getCrop(cropName).getBreaches(sensor, value);

        breaches.addAll(
                crops.getFacilityRules().getBreaches(sensor, value)
        );

        return breaches;
    }

    private void handleBreaches(
            String chamber,
            String cropName,
            ArrayList<Threshold> breaches) {

        for (Threshold t : breaches) {
            String message = chamber + " " + t.getAlertLabel();

            events.recordAlert(chamber, cropName, message);

            ControlAction action = t.getAction();

            usage.addWater(action.getWaterCost());
            usage.addEnergy(action.getEnergyCost());

            if (action.getDevice() != null) {
                SVFRMS.addControlRequest(chamber, action.getDevice());
            }
        }
    }
}
