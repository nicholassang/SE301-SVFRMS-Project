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
    // final: set once in the constructor, then safely visible to all 4 chamber threads
    private final ChamberRepository chambers;
    private final CropRegister crops;
    private final ResourceUsageTracker usage;
    private final EventRecorder events;
    private final ControlQueue controlQueue;

    public SensorMonitor(ChamberRepository chambers, CropRegister crops, ResourceUsageTracker usage, EventRecorder events, ControlQueue controlQueue){
        this.chambers = chambers;
        this.crops = crops;
        this.usage = usage;
        this.events = events;
        this.controlQueue = controlQueue;
    }

    public void processSensor(String chamber, String cropName, String sensor, double value){
        events.logSensor("Processing " + cropName + " " + sensor + " = " + value);
        chambers.getChamberState(chamber).setState(sensor, value);

        ArrayList<Threshold> breaches = crops.getCrop(cropName).getBreaches(sensor, value);
        breaches.addAll(crops.getFacilityRules().getBreaches(sensor, value));

        for(Threshold t : breaches){
            String message = chamber + " " + t.getAlertLabel();
            events.recordAlert(chamber, cropName, message);
            usage.addWater(t.getWaterCost());
            usage.addEnergy(t.getEnergyCost());
            if(t.getDevice()!= null){
                controlQueue.submit(chamber, t.getDevice());
            }
        }
    } 


}
