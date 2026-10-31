package org.example;

import java.util.HashMap;

public class ChamberState {
    private final HashMap<String, Double> readings = new HashMap<>();   // guarded by synchronized methods

    // stores the current readings for one chamber
    public ChamberState(){
        readings.put("Temperature", 24.0);
        readings.put("Humidity", 70.0);
        readings.put("Soil", 60.0);
        readings.put("Light", 600.0);
        readings.put("Nutrient", 70.0);
    }

    public synchronized double getState(String sensor){
        return readings.get(sensor);
    }

    public synchronized void setState(String sensor, double value){
        readings.put(sensor, value);
    }

    // synchronized to avoid 2 threads overlaping on the same chamber when reading & writing, then return new value
    public synchronized double adjust(String sensor, double change, double min, double max){
        double newValue = readings.get(sensor) + change;
        
        // set the min and the max for the Automated Devices (lagecy code line 84)
        if(newValue < min){
            newValue = min;
        }
        if(newValue > max){
            newValue = max;
        }
        readings.put(sensor, newValue);
        return newValue;
    }
}
