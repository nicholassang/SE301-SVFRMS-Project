package org.example;

import java.util.ArrayList;

public class ThresholdRules {
    private ArrayList<Threshold> thresholds = new ArrayList<>();

    public void addRule(Threshold threshold) {
        thresholds.add(threshold);
    }

    public ArrayList<Threshold> getBreaches(
            SensorType sensor, double value) {

        ArrayList<Threshold> result = new ArrayList<>();

        for (Threshold t : thresholds) {
            if (t.isBreached(sensor, value)) {
                result.add(t);
            }
        }

        return result;
    }
}