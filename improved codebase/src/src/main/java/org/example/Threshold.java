package org.example;

// stating a rule to solve the hard coded rules for every crops 
public class Threshold {
    private final SensorType sensor;
    private final ThresholdType type;
    private final double limit;
    private final String alertLabel;
    private final ControlAction action;

    public Threshold(
            SensorType sensor,
            ThresholdType type,
            double limit,
            String alertLabel,
            ControlAction action) {

        this.sensor = sensor;
        this.type = type;
        this.limit = limit;
        this.alertLabel = alertLabel;
        this.action = action;
    }

    public String getAlertLabel(){
        return alertLabel;
    }

    public ControlAction getAction() {
        return action;
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
