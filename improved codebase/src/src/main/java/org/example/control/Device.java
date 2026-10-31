package org.example.control;

import org.example.sensor.SensorType;
import org.example.chamber.ChamberState;

// reading it changes by how much and its limits
public class Device {
    // all final: a Device never changes after creation, so threads can share it without locking
    private final String name;          // e.g. "Cooling Fan"
    private final SensorType sensor;        // e.g. "Temperature"
    private final double change;        // e.g. -5
    private final double min;
    private final double max;
    private final String messageStart;  // e.g. "Temperature reduced to "
    private final String unit;          // e.g. "C"

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
