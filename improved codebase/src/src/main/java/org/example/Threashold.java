package org.example;

// stating a rule to solve the hard coded rules for every crops 
public class Threashold {
    private String sensor;        // Temperature, humidity, etc
    private boolean isUpperLimit; // true = alert when ABOVE, false = alert when BELOW
    private double limit;
    private String alertLabel;   // eg. "Tomato High Temp"
    private String device;       // eg. Cooling Fan, or null if no device
    private int waterCost;
    private int energyCost; 

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
