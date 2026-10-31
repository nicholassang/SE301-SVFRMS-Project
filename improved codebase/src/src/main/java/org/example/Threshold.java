package org.example;

// stating a rule to solve the hard coded rules for every crops 
public class Threshold {
    // all final: a rule never changes after creation, so the 4 chamber threads can share it without locking
    private final String sensor;        // Temperature, humidity, etc
    private final boolean isUpperLimit; // true = alert when ABOVE, false = alert when BELOW
    private final double limit;
    private final String alertLabel;   // eg. "Tomato High Temp"
    private final String device;       // eg. Cooling Fan, or null if no device
    private final int waterCost;
    private final int energyCost;

    public Threshold(String sensor, boolean isUpperLimit, double limit,
                     String alertLabel, String device, int waterCost, int energyCost) {
        this.sensor = sensor;
        this.isUpperLimit = isUpperLimit;
        this.limit = limit;
        this.alertLabel = alertLabel;
        this.device = device;
        this.waterCost = waterCost;
        this.energyCost = energyCost;
    }

    public String getAlertLabel(){
        return alertLabel;
    }

    public String getDevice(){
        return device;
    }

    public int getWaterCost(){
        return waterCost;
    }

    public int getEnergyCost(){
        return energyCost;
    }


    public boolean isBreached(String sensorName, double value) {
        if(!sensor.equals(sensorName)){
            return false;
        }

        if (isUpperLimit){
             return value > limit;
        }
        return value < limit;
    }


}
