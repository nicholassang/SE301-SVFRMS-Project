package org.example;

import java.util.ArrayList;

public class FacilityRules {
    private final ThresholdRules rules = new ThresholdRules();

    public void addRule(Threshold threshold) {
        rules.addRule(threshold);
    }

    public ArrayList<Threshold> getBreaches(
            SensorType sensor, double value) {

        return rules.getBreaches(sensor, value);
    }
}