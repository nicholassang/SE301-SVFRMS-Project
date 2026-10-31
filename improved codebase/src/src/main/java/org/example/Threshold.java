package org.example;

// stating a rule to solve the hard coded rules for every crops 
public class Threshold {
    private SensorType sensor;        // Temperature, humidity, etc
    private ThresholdType type; // true = alert when ABOVE, false = alert when BELOW
    private double limit;
    private String alertLabel;   // eg. "Tomato High Temp"
    private String device;       // eg. Cooling Fan, or null if no device
    private int waterCost;
    private int energyCost; 

    public Threshold(SensorType sensor, ThresholdType type, double limit,
                     String alertLabel, String device, int waterCost, int energyCost) {
        this.sensor = sensor;
        this.type = type;
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


    public boolean isBreached(SensorType sensorName, double value) {
        if(sensor != sensorName){
            return false;
        }

        return switch (type) {
            case ABOVE -> value > limit;
            case BELOW -> value < limit;
        };
    }


}
