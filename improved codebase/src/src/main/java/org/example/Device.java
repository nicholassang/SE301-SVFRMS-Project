package org.example;


// reading it changes by how much and its limits
public class Device {
    private String name;          // e.g. "Cooling Fan"
    private SensorType sensor;        // e.g. "Temperature"
    private double change;        // e.g. -5
    private double min;
    private double max;
    private String messageStart;  // e.g. "Temperature reduced to "
    private String unit;          // e.g. "C"

    public Device(String name, SensorType sensor, double change, double min, double max,
                  String messageStart, String unit){
        this.name = name;
        this.sensor = sensor;
        this.change = change;
        this.min = min;
        this.max = max;
        this.messageStart = messageStart;
        this.unit = unit;
    }

    public String getName(){
        return name;
    }


    // changes the chamber and returns the result message
    public String activate(ChamberState state){
        double newValue = state.adjust(sensor, change, min, max);
        return messageStart + newValue + unit;
    }


}
