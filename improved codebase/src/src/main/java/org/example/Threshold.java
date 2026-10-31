package org.example;

// stating a rule to solve the hard coded rules for every crops 
public class Threshold {
    private final SensorType sensor;
    private final ThresholdType thresholdType;
    private final double limit;
    private final String alertLabel;
    private final ControlAction controlAction;

    public Threshold(
            SensorType sensor,
            ThresholdType thresholdType,
            double limit,
            String alertLabel,
            ControlAction controlAction) {

        this.sensor = sensor;
        this.thresholdType = thresholdType;
        this.limit = limit;
        this.alertLabel = alertLabel;
        this.controlAction = controlAction;
    }

    public String getRuleId() {
        return sensor.name() + ":" + thresholdType.name() + ":" + limit;
    }

    public double calculateDeviation(double value) {
        return Math.abs(value - limit);
    }

    public String getAlertLabel(){
        return alertLabel;
    }

    public ControlAction getControlAction() {
        return controlAction;
    }

    public boolean isBreached(SensorType sensorName, double value) {
        if(sensor != sensorName){
            return false;
        }

        return switch (thresholdType) {
            case ABOVE -> value > limit;
            case BELOW -> value < limit;
        };
    }


}
