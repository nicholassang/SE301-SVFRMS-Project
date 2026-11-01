package org.example.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThresholdRules {
    private final List<Threshold> thresholds = new CopyOnWriteArrayList<>();

    public void addRule(Threshold threshold) {
        thresholds.add(threshold);
    }

    public ArrayList<Threshold> findBreaches(SensorType sensor, double value) {
        ArrayList<Threshold> result = new ArrayList<>();
        for (Threshold t : thresholds) {
            if (t.isBreached(sensor, value)) {
                result.add(t);
            }
        }
        return result;
    }
}
