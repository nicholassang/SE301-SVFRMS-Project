package org.example.control;

import org.example.sensor.SensorType;
import org.example.chamber.ChamberState;

public class Device {
    private final String name;          
    private final SensorType sensor;        
    private final double change;        
    private final double min;
    private final double max;
    private final String messageStart;  
    private final String unit;          

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

    public String activate(ChamberState state){
        double newValue = state.adjust(sensor, change, min, max);
        return messageStart + newValue + unit;
    }
}
