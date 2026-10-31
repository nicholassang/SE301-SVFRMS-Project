package org.example.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThresholdRules {
    // read by all 4 chamber threads on every reading, written rarely:
    // CopyOnWriteArrayList lets them loop safely even while a rule is being added (same as CropProfile)
    private final List<Threshold> thresholds = new CopyOnWriteArrayList<>();

    public void addRule(Threshold threshold) {
        thresholds.add(threshold);
    }

    public ArrayList<Threshold> findBreaches(
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
