package org.example;

import java.util.HashMap;

public class ChamberState {
    private HashMap<SensorType, Double> readings = new HashMap<>();

    // stores the current readings for one chamber
    public ChamberState(){
        readings.put(SensorType.TEMPERATURE, 24.0);
        readings.put(SensorType.HUMIDITY, 70.0);
        readings.put(SensorType.SOIL, 60.0);
        readings.put(SensorType.LIGHT, 600.0);
        readings.put(SensorType.NUTRIENT, 70.0);
    }

    public synchronized double getState(SensorType sensor){
        return readings.get(sensor);
    }

    public synchronized void setState(SensorType sensor, double value){
        readings.put(sensor, value);
    }

    // synchronized to avoid 2 threads overlaping on the same chamber when reading & writing, then return new value
    public synchronized double adjust(SensorType sensor, double change, double min, double max){
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
