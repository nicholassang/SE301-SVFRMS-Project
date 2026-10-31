package org.example.crop;

import org.example.sensor.Threshold;
import org.example.sensor.ThresholdRules;
import org.example.sensor.SensorType;

import java.util.ArrayList;

public class FacilityRules {
    private final ThresholdRules rules = new ThresholdRules();

    public void addRule(Threshold threshold) {
        rules.addRule(threshold);
    }

    public ArrayList<Threshold> findBreaches(
            SensorType sensor, double value) {

        return rules.findBreaches(sensor, value);
    }
}
